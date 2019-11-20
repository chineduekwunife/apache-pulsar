# Apache Pulsar Demo
Simulates publish subscribe use cases in Apache Pulsar. Also provides easy redeployment of services using Watchtower.

### Pulling Latest Updates
```
docker-compose -f pulsar.yml pull
```

### Clear Dangling Docker images (run from time to time)
```
docker volume rm $(docker volume ls -qf dangling=true)
docker system prune -f
```

### Running the demo

- **Step 1**. Start local Docker registry 
```
docker-compose -f docker-registry.yml up -d
```

- **Step 2**. Login to the local Docker registry. This authentication will be used by Watchtower to pull images from local registry.

```
docker login 127.0.0.1:5000 -u test -p test
```

- **Step 3**. Build, tag and push `pulsar-producer` and `pulsar consumer` to the local Docker registry.

```
./gradlew clean deployDocker
```

- **Step 4**. Start `pulsar-producer` and `pulsar consumer` services

```
docker-compose -f pulsar.yml up
```

- **Step 5**. Confirm that request can be sent from `pulsar-producer` to `pulsar-consumer` via `pulsar`. The following request should produce some
output in `pulsar-consumer` logs

```
curl -X GET "http://localhost:8081/ws/pulsar" -H "accept: */*"
```

- **Step 6**. When you make code changes to `pulsar-producer` or `pulsar-consumer`, redeploy the respective container by following Step 3 above. Observe from the logs that Watchtower will stop the existing container and automatically deploy a new one containing your code changes.

- **Step 7**. To view pulsar dashboard, navigate to localhost on the browser
