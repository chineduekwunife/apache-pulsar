# Apache Pulsar Geo Replication Demo
Simulates running Pulsar with an already existing zookeeper.

### Clear Dangling Docker images (run from time to time)
```
docker volume rm $(docker volume ls -qf dangling=true)
docker system prune -f
```

### Running the Apache Pulsar Demo

- **Step 1**. Create a network which will be used by the components to communicate.

```
docker network create datacenter-network
```

- **Step 2**. Start cluster zookeeper
```
cd integration/pulsar-with-external-zookeeper/
docker-compose -f zookeeper.yml up
```

- **Step 3**. Start pulsar proxy required to connect to zookeeper and initialize the cluster
```
cd integration/pulsar-with-external-zookeeper/
docker-compose -f pulsar-proxy.yml up
```

- **Step 4**. Initialize cluster metadata from a Pulsar CLI.

```
docker exec -it pulsar-proxy bin/pulsar initialize-cluster-metadata \
      --cluster demo-cluster \
      --zookeeper zk:2181 \
      --configuration-store zk:2181 \
      --web-service-url http://broker:8080 \
      --broker-service-url pulsar://broker:6650
```

- **Step 5**. Start cluster components

```
cd integration/pulsar-with-external-zookeeper/
docker-compose -f pulsar-components.yml up
```
  

- **Step 6**. Verify messages send and receive. 

```
docker exec -it broker /bin/bash
bin/pulsar-client --url pulsar://broker:6650 consume -s "sub-test" my-topic -n 0
```

```
docker exec -it bk /bin/bash
bin/pulsar-client --url pulsar://broker:6650 produce my-topic --messages "hello-from-us" -n 10
```

You will see that produced messages are deployed.

