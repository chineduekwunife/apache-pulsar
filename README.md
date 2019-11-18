# Documentation:
https://pulsar.apache.org/docs/en/standalone-docker/

# Run application:
./gradlew services:api:bootRun

# Test with curl:
curl -X GET "http://localhost:8080/ws/100" -H "accept: */*"

# Test with Swagger:
localhost:8080/swagger-ui.html

# Design Decision:
Asynchronous API operations are achieved by sending a message to Async Actor, which in turn allocates the task to a pool of worker actors in a round robbin fashion.

