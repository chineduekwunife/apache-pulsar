# Apache Pulsar Geo Replication Demo
Simulates Geo Replication in a Pulsar Multi-Cluster with global configuration store (global zookeeper) and one broker and bookeeper in each cluster.

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

- **Step 1**. Create a network which will be used by both data-centers to communicate

```
docker network create datacenter-network
```

- **Step 2**. Start `Global` zookeeper and initialize cluster metadata for `US` and `EU` clusters.
```
cd integration/geo-replication-with-global-config-store/
cd global-config
docker-compose -f pulsar-global-zk.yml up
```

```
docker exec -it zk-global bin/pulsar zookeeper-shell create /cluster-us data

docker exec -it zk-global bin/pulsar initialize-cluster-metadata \
      --cluster cluster-us \
      --zookeeper zk-global:2181/cluster-us \
      --configuration-store zk-global:2181 \
      --web-service-url http://broker-us:8080 \
      --broker-service-url pulsar://broker-us:6650
```

```
docker exec -it zk-global bin/pulsar zookeeper-shell create /cluster-eu data

docker exec -it zk-global bin/pulsar initialize-cluster-metadata \
      --cluster cluster-eu \
      --zookeeper zk-global:2181/cluster-eu \
      --configuration-store zk-global:2181 \
      --web-service-url http://broker-eu:8080 \
      --broker-service-url pulsar://broker-eu:6650
```

- **Step 3**. Start `US` and `EU` cluster components

```
cd integration/geo-replication-with-global-config-store/
cd us-datacenter
docker-compose -f pulsar-us-components.yml up
```

```
cd integration/geo-replication-with-global-config-store/
cd eu-datacenter
docker-compose -f pulsar-eu-components.yml up
```
  
- **Step 4**. Create tenants and namespaces. This needs to be done on one broker since config will be stored on global zookeeper and thus replicated.

```
docker exec -it broker-us bin/pulsar-admin tenants create my-tenant --allowed-clusters cluster-us,cluster-eu
docker exec -it broker-us bin/pulsar-admin namespaces create my-tenant/my-namespace --clusters cluster-us,cluster-eu
```

- **Step 5**. [OPTIONAL] Prepare the broker clients on each cluster to target the correct broker. This is required for testing pub-sub, but can be 
skipped by providing the `url` of the brokers for publishing and subscribing.

```
docker exec -it broker-us /bin/bash
sed -i "s/localhost/broker-us/g" conf/client.conf
cat conf/client.conf
exit
```

```
docker exec -it broker-eu /bin/bash
sed -i "s/localhost/broker-eu/g" conf/client.conf
cat conf/client.conf
exit
```

- **Step 6**. Verify messages send and receive. In cluster-eu, create a subscription for the target topic, which will wait to receive messages from cluster-us

In cluster-eu, listen for messages sent to target tenant/namespace/topic from cluster-us. If you have prepared the broker clients in Step 5, do:
```
docker exec -it broker-eu bin/pulsar-client consume -s "sub-test" my-tenant/my-namespace/my-topic -n 0
```

otherwise provide the broker url

```
docker exec -it broker-eu bin/pulsar-client --url pulsar://broker-eu:6650 consume -s "sub-test" my-tenant/my-namespace/my-topic -n 0
```


In cluster-us, produce message to the target tenant/namespace/topic. If you have prepared the broker clients in Step 5, do:
```
docker exec -it broker-us bin/pulsar-client produce  my-tenant/my-namespace/my-topic  --messages "hello-from-us" -n 10
```

otherwise provide the broker url

```
docker exec -it broker-us bin/pulsar-client --url pulsar://broker-us:6650 produce  my-tenant/my-namespace/my-topic  --messages "hello-from-us" -n 10
```

You will see that messages are received in cluster-eu.

