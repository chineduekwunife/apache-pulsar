# Apache Pulsar Demo
Simulates publish-subscribe use cases in Apache Pulsar. Also provides easy redeployment of services using Watchtower.

### Pulling Latest Updates
```
docker-compose -f pulsar.yml pull
```

### Clear Dangling Docker images (run from time to time)
```
docker volume rm $(docker volume ls -qf dangling=true)
docker system prune -f
```

### Running the Apache Pulsar Demo


- **Step 1**. Start local Docker registry 
```
docker-compose -f docker-registry.yml up -d
```

- **Step 2**. Login to the local Docker registry. This authentication will be used by Watchtower to pull images from local registry (required only once)

```
docker login 127.0.0.1:5000 -u test -p test
```

- **Step 3**. Build, tag and push `pulsar-producer` and `pulsar-consumer` to the local Docker registry.

```
./gradlew clean deployDocker
```

- **Step 4**. Start `pulsar-producer` and `pulsar-consumer` services

```
docker-compose -f pulsar.yml up
```

- **Step 5**. Confirm that request can be sent from `pulsar-producer` to `pulsar-consumer` via `pulsar`. The following request should produce some
output in `pulsar-consumer` logs

```
curl -X GET "http://localhost:8081/ws/pulsar"
```

- **Step 6**. When you make code changes to `pulsar-producer` or `pulsar-consumer`, redeploy the respective container by following Step 3 above. Observe from the logs that Watchtower will stop the existing container and automatically deploy a new one containing your code changes.

- **Step 7**. To view pulsar dashboard, navigate to localhost on the browser


### Running the Apache Pulsar - Apache Spark Streaming Demo

Aim is to attach a pipe to Pulsar's topic(s) (streaming-topic in this example) so that any data sent to the topic(s) can be processed in realtime by Spark.

- **Step 1**. Stop any running docker containers
```
Ctrl+C
docker-compose -f pulsar.yml down
```

- **Step 2**. Install Apache Spark - see https://www.tutorialkart.com/apache-spark/how-to-install-spark-on-mac-os/

- **Step 3**. Install and start Apache Pulsar

```
wget https://archive.apache.org/dist/pulsar/pulsar-2.4.1/apache-pulsar-2.4.1-bin.tar.gz
tar xvfz apache-pulsar-2.4.1-bin.tar.gz
cd apache-pulsar-2.4.1
bin/pulsar standalone
```

If any issue occurs while starting up pulsar, add 127.0.0.1 <laptop name>.local to /etc/hosts or whatever name it errors out on your laptop - see 
https://github.com/apache/pulsar/issues/5144

- **Step 4**. Build and submit the Pulsar Spark Streaming job

```
./gradlew services:spark:clean
./gradlew services:spark:shadowJar
/usr/local/Cellar/apache-spark/2.4.4/bin/spark-submit --class com.pulsar.spark.stream.pulsar.PulsarStreamProcessor --master local[2] services/spark/build/libs/spark-1.0.0-SNAPSHOT-all.jar
```

- **Step 5**. Send messages to the Pulsar topic(s) being streamed and consumed in Spark, note from the Spark logs that the data in the stream was received and processed

```
 Ensure pulsar-producer application is running (you can run from IDE), and then:
  
 curl -X GET "http://localhost:8081/ws/pulsar-spark-stream"
```



