# Apache Pulsar Geo Replication Demo
Simulates Geo Replication in a Pulsar Multi-Cluster with multiple global configuration store (global zookeepers) and two brokers and bookeepers in each cluster.

### Clear Dangling Docker images (run from time to time)
```
docker volume rm $(docker volume ls -qf dangling=true)
docker system prune -f
```

### Running the Apache Pulsar Demo

- **Step 1**. Create a network which will be used by both data-centers to communicate.

```
docker network create datacenter-network
```

- **Step 2**. Start `US` and `EU` cluster zookeepers
```
cd integration/geo-replication-with-multiple-global-config-store-multiple-components/
cd us-datacenter
docker-compose -f pulsar-us-zk.yml up
```

```
cd integration/geo-replication-with-multiple-global-config-store-multiple-components/
cd eu-datacenter
docker-compose -f pulsar-eu-zk.yml up
```

- **Step 3**. Set up Zookeeper quorum for US cluster, by adding all ZooKeeper servers to conf/zookeeper.conf.  
 Also on each host, we need to specify the ID of the node in each node's myid file, which is in each server's data/zookeeper folder by default 
 (this can be changed via the dataDir parameter).

```
docker exec -it zk1-us /bin/bash

apt-get update
apt-get -y install nano

nano conf/zookeeper.conf

Paste below lines
server.1=zk1-us:2888:3888
server.2=zk2-us:2888:3888
server.3=zk3-us:2888:3888

mkdir -p data/zookeeper
echo 1 > data/zookeeper/myid
exit
```

```
docker exec -it zk2-us /bin/bash

apt-get update
apt-get -y install nano

nano conf/zookeeper.conf

Paste below lines
server.1=zk1-us:2888:3888
server.2=zk2-us:2888:3888
server.3=zk3-us:2888:3888

mkdir -p data/zookeeper
echo 2 > data/zookeeper/myid
exit
```

```
docker exec -it zk3-us /bin/bash

apt-get update
apt-get -y install nano

nano conf/zookeeper.conf

Paste below lines
server.1=zk1-us:2888:3888
server.2=zk2-us:2888:3888
server.3=zk3-us:2888:3888

mkdir -p data/zookeeper
echo 3 > data/zookeeper/myid
exit
```

```
Restart docker to pick up new config

docker restart zk1-us;docker restart zk2-us;docker restart zk3-us
```

- **Step 4**. Set up Zookeeper quorum for EU cluster, by adding all ZooKeeper servers to conf/zookeeper.conf.  
 Also on each host, we need to specify the ID of the node in each node's myid file, which is in each server's data/zookeeper folder by default 
 (this can be changed via the dataDir parameter).

```
docker exec -it zk1-eu /bin/bash

apt-get update
apt-get -y install nano

nano conf/zookeeper.conf

Paste below lines
server.1=zk1-eu:2888:3888
server.2=zk2-eu:2888:3888
server.3=zk3-eu:2888:3888

mkdir -p data/zookeeper
echo 1 > data/zookeeper/myid
exit
```

```
docker exec -it zk2-eu /bin/bash

apt-get update
apt-get -y install nano

nano conf/zookeeper.conf

Paste below lines
server.1=zk1-eu:2888:3888
server.2=zk2-eu:2888:3888
server.3=zk3-eu:2888:3888

mkdir -p data/zookeeper
echo 2 > data/zookeeper/myid
exit
```

```
docker exec -it zk3-eu /bin/bash

apt-get update
apt-get -y install nano

nano conf/zookeeper.conf

Paste below lines
server.1=zk1-eu:2888:3888
server.2=zk2-eu:2888:3888
server.3=zk3-eu:2888:3888

mkdir -p data/zookeeper
echo 3 > data/zookeeper/myid
exit
```

```
Restart docker to pick up new config

docker restart zk1-eu;docker restart zk2-eu;docker restart zk3-eu
```

- **Step 5**. Set up zookeeper quorum for global configuration servers, by adding all ZooKeeper servers to conf/global_zookeeper.conf. 
 On each host, we need to specify the ID of the node in each node's myid file, which is in each server's data/global-zookeeper folder by default 
 (this can be changed via the dataDir parameter). Client port is already set to 2184, so no need to set this again.

```
cd integration/geo-replication-with-multiple-global-config-store-multiple-components/
cd global-config
docker-compose -f pulsar-global-zk.yml up
```


```
docker exec -it zk1-global /bin/bash

apt-get update
apt-get -y install nano

nano conf/global_zookeeper.conf

Paste below lines
server.1=zk1-global:2185:2186
server.2=zk2-global:2185:2186
server.3=zk3-global:2185:2186

mkdir -p data/global-zookeeper
echo 1 > data/global-zookeeper/myid
exit
```

```
docker exec -it zk2-global /bin/bash

apt-get update
apt-get -y install nano

nano conf/global_zookeeper.conf

Paste below lines
server.1=zk1-global:2185:2186
server.2=zk2-global:2185:2186
server.3=zk3-global:2185:2186

mkdir -p data/global-zookeeper
echo 2 > data/global-zookeeper/myid
exit
```

```
docker exec -it zk3-global /bin/bash

apt-get update
apt-get -y install nano

nano conf/global_zookeeper.conf

Paste below lines
server.1=zk1-global:2185:2186
server.2=zk2-global:2185:2186
server.3=zk3-global:2185:2186

mkdir -p data/global-zookeeper
echo 3 > data/global-zookeeper/myid
exit
```

```
Restart docker to pick up new config

docker restart zk1-global;docker restart zk2-global;docker restart zk3-global
```

- **Step 6**. Initialize cluster metadata for `US` and `EU` clusters.

```
docker exec -it zk1-us bin/pulsar initialize-cluster-metadata \
      --cluster cluster-us \
      --zookeeper zk1-us:2181 \
      --configuration-store zk1-global:2184 \
      --web-service-url http://broker1-us:8080 \
      --broker-service-url pulsar://broker1-us:6650
```

```
docker exec -it zk1-eu bin/pulsar initialize-cluster-metadata \
      --cluster cluster-eu \
      --zookeeper zk1-eu:2181 \
      --configuration-store zk1-global:2184 \
      --web-service-url http://broker1-eu:8080 \
      --broker-service-url pulsar://broker1-eu:6650
```

- **Step 7**. Start `US` and `EU` cluster components

```
cd integration/geo-replication-with-multiple-global-config-store-multiple-components/
cd us-datacenter
docker-compose -f pulsar-us-components.yml up
```

```
cd integration/geo-replication-with-multiple-global-config-store-multiple-components/
cd eu-datacenter
docker-compose -f pulsar-eu-components.yml up
```
  
- **Step 8**. Create tenants and namespaces. This needs to be done on one broker since config will be stored on global zookeeper and thus replicated.

```
docker exec -it broker2-us bin/pulsar-admin tenants create my-tenant --allowed-clusters cluster-us,cluster-eu
docker exec -it broker2-us bin/pulsar-admin namespaces create my-tenant/my-namespace --clusters cluster-us,cluster-eu
```

- **Step 9**. [OPTIONAL] Prepare the broker clients on each cluster to target the correct broker. This is required for testing pub-sub, but can be 
skipped by providing the `url` of the brokers for publishing and subscribing.

```
docker exec -it broker2-us /bin/bash
sed -i "s/localhost/broker-us/g" conf/client.conf
cat conf/client.conf
exit
```

```
docker exec -it broker2-eu /bin/bash
sed -i "s/localhost/broker-eu/g" conf/client.conf
cat conf/client.conf
exit
```

- **Step 10**. Verify messages send and receive. In cluster-eu, create a subscription for the target topic, which will wait to receive messages from cluster-us

In cluster-eu, listen for messages sent to target tenant/namespace/topic from cluster-us. If you have prepared the broker clients in Step 5, do:
```
docker exec -it broker2-eu bin/pulsar-client consume -s "sub-test" my-tenant/my-namespace/my-topic -n 0
```

otherwise provide the broker url

```
docker exec -it broker2-eu bin/pulsar-client --url pulsar://broker2-eu:6650 consume -s "sub-test" my-tenant/my-namespace/my-topic -n 0
```


In cluster-us, produce message to the target tenant/namespace/topic. If you have prepared the broker clients in Step 5, do:
```
docker exec -it broker2-us bin/pulsar-client produce  my-tenant/my-namespace/my-topic  --messages "hello-from-us" -n 10
```

otherwise provide the broker url

```
docker exec -it broker2-us bin/pulsar-client --url pulsar://broker2-us:6650 produce  my-tenant/my-namespace/my-topic  --messages "hello-from-us" -n 10
```

You will see that messages are received in cluster-eu.

