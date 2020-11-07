# API

1. GET `/getAvailableDates?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd` provides information of the availability of the campsite
 for a given date range with the default being 1 month, specifically the list of dates that are available for booking.
  * Due to the time constraint spent on the project, the assumptions: `startDate` and `endDate`, if present, are valid
  date strings and `startDate` < `endDate`. Otherwise, return an error without much info.
2. POST `/` with required payload=`{"startDate": "yyyy-MM-dd", "endDate": "yyyy-MM-dd", "email": "test@email.com", "name": "test"}`.
  * Assumptions: `startDate` and `endDate` are valid date strings and `startDate` < `endDate`;
  `startDate` is within 3 days of `endDate`;
  current date must be within 1 - 30 days ahead of the `startDate`. Otherwise, return an error.
  If successful, return the reservation id;
3. GET `/{reservationId}` returns the reservation details (start date, end date, name and email).
4. DELETE `/{reservationId}` deletes the reservation. Return the reservation that is being deleted or `null` if there
is no such reservation.
5. PUT `/{reservationId}` with payload=`{"startDate": "yyyy-MM-dd", "endDate": "yyyy-MM-dd", "email": "test@email.com", "name": "test"}`
  * Assumptions: `email` or `name` is optional. The most important business logic of updating a reservation is that the
  campsite must be available during the new period. If successful, return the new reservation details. Otherwise, return `null`.

## Discussion around concurrent requests

Multiple users can attempt to book or modify the campsite for the same/overlapping dates concurrently. In this basic service 
implementation, the reservation records are retrieved and then they are processed to determine if any record overlaps with some period that the user is trying to book.
The problem with this implementation is there can be split milisecond when right after the records are retrieved, there
is a new record being saved that results in a conflict. How do deal with this?
* We can have some locking mechanism either at database or application level. However, locking or synchronization leads
to low throughput, which is not acceptable.
* Another solution is to leverage some `eventual consistency` mechanism when the system basically accepts the request
to create or modify a reservation upon checking the business logic and then there is another process that will verify
the integrity of the system to reject some creation or modification.
Asynchronous Command and Query Responsibility Segregation (CQRS) pattern can help as we have a lot more reads than
writes for the system.

## Discussion around scalability of the service

Without locking or synchronization that reduces the throughput of the service, the service built on Spring Boot should
be able to handle 100 transactions per second (TPS) just fine with a load balancer sitting in front to direct requests to
a handful of application servers. It is pretty easy these days to provision application servers on demand to achieve
horizontal scaling due to the advent of on-demand VMs and Kubernetes pods especially.

## Run this service locally
**Please ote that for demonstration purposes, the current date is assumed to be `2020-11-07`.**

`cd ./campsite` and then:

1. `./mvnw spring-boot:run -Dspring-boot.run.arguments="init"` to run the application at `http://localhost:8080` with
a few existing reservation records.
  * ```
    reservationRepository.save(new Reservation("email1Id", "2020-11-11", "2020-11-14", "email1@test.com", "email1"));
    reservationRepository.save(new Reservation("email2Id", "2020-11-16", "2020-11-17", "email2@test.com", "email2"));
    reservationRepository.save(new Reservation("email3Id", "2020-11-01", "2020-11-09", "email3@test.com", "email3"));
    reservationRepository.save(new Reservation("email4Id", "2020-11-20", "2020-11-25", "email4@test.com", "email4"));
    ```
2. `./mvnw spring-boot:run` to run the application at `http://localhost:8080` with no existing reservation records.

Using `java` command if one does not wish to install `maven`:
1. `java -jar ./campsite-0.0.1-SNAPSHOT.jar init` to run the application at `http://localhost:8080` with
a few existing reservation records.
2. `java -jar ./campsite-0.0.1-SNAPSHOT.jar` to run the application at `http://localhost:8080` with no existing reservation records.

## Sample requests
1. To get available dates in November, 2020 with the presence of existing records:
    ```
    curl --location --request GET 'http://localhost:8080/v1/booking/getAvailableDates?startDate=2020-11-01&endDate=2020-11-30' \
    --header 'Content-Type: application/json'
    ```
2. To get the reservation with id `email1Id`:
    ```
    curl --location --request GET 'http://localhost:8080/v1/booking/email1Id' \
    --header 'Content-Type: application/json'
    ```
3. To create a *valid* reservation:
    ```
    curl --location --request POST 'http://localhost:8080/v1/booking' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "startDate": "2020-11-18",
        "endDate": "2020-11-19",
        "email": "email5@test.com",
        "name": "email5"
    }'
    ```
   Conflict dates:
   ```
   curl --location --request POST 'http://localhost:8080/v1/booking' \
   --header 'Content-Type: application/json' \
   --data-raw '{
       "startDate": "2020-11-20",
       "endDate": "2020-11-21",
       "email": "email6@test.com",
       "name": "email6"
   }'
   ```
   More than 3 days in the booking period:
    ```
    curl --location --request POST 'http://localhost:8080/v1/booking' \
    --header 'Content-Type: application/json' \
    --data-raw '{
      "startDate": "2020-11-26",
      "endDate": "2020-11-30",
      "email": "email6@test.com",
      "name": "email6"
    }'
    ```
   Too far in advance
    ```
    curl --location --request POST 'http://localhost:8080/v1/booking' \
    --header 'Content-Type: application/json' \
    --data-raw '{
      "startDate": "2020-12-15",
      "endDate": "2020-12-16",
      "email": "email7@test.com",
      "name": "email7"
    }'
    ```
4. Valid Update a reservation:
    ```
    curl --location --request PUT 'http://localhost:8080/v1/booking/email1Id' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "startDate": "2020-11-10",
        "endDate": "2020-11-12"
    }'
    ```
   Invalid update a reservation due to unavailability:
    ```
    curl --location --request PUT 'http://localhost:8080/v1/booking/email1Id' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "startDate": "2020-11-10",
        "endDate": "2020-11-20"
    }'
    ```
5. To delete the reservation with id `email1Id`:
```
curl --location --request DELETE 'http://localhost:8080/v1/booking/email2Id' \
--header 'Content-Type: application/json' \
--data-raw ''
```
Right after the deletion, getting that reservation again results in a `null` response.

## Limitation
Due to time constraint, the system has these limitations:
1. Cannot handle concurrent requests that might result in overlapping booking periods. However, a solution is discussed
above.
2. Error handling is primitive, especially an insightful error message hasn't been returned when there is some internal
server error. However, with more time, the @ControllerAdvice or @RestControllerAdvice pattern will be used to return
insightful error messages. Different kinds of business logic error are represented by different Exceptions in the util folder.
