package com.pulsar.spark.stream.pulsar

import java.util

import com.pulsar.model.constants.Subscription.STREAM_SUBSCRIPTION
import com.pulsar.model.constants.Topic.STREAMING_TOPIC
import org.apache.pulsar.client.api.{SubscriptionInitialPosition, SubscriptionType}
import org.apache.pulsar.client.impl.conf.ConsumerConfigurationData
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Attaches a pipe to Pulsar's topic(s) (streaming-topic in this example) so that any data sent to the topic(s) can be processed in realtime by Spark.
  *
  * To test:
  * 1. Install Apache Spark - see https://www.tutorialkart.com/apache-spark/how-to-install-spark-on-mac-os/
  *
  * 2. Install and start Apache Pulsar
  *
  * wget https://archive.apache.org/dist/pulsar/pulsar-2.4.1/apache-pulsar-2.4.1-bin.tar.gz
  * tar xvfz apache-pulsar-2.4.1-bin.tar.gz
  * cd apache-pulsar-2.4.1
  * bin/pulsar standalone
  *
  * If any issues while starting up pulsar,
  * add 127.0.0.1 <laptop name>.local to /etc/hosts or whatever name it errors out on your laptop (see https://github.com/apache/pulsar/issues/5144)
  *
  *
  * 3. Build and submit the Pulsar Spark Streaming job
  * ./gradlew services:spark:clean
  * ./gradlew services:spark:shadowJar
  *
  * /usr/local/Cellar/apache-spark/2.4.4/bin/spark-submit --class com.pulsar.spark.stream.pulsar.PulsarStreamProcessor --master local[2] services/spark/build/libs/spark-1.0.0-SNAPSHOT-all.jar
  *
  *
  * 4. Send messages to the Pulsar topic(s) being streamed and consumed in Spark, note from the Spark logs that the data in the stream was received and processed
  *
  *  Ensure pulsar-producer application is running (you can run from IDE), and then:
  *
  *  curl -X GET "http://localhost:8081/ws/pulsar-spark-stream"
  *
  *  @author Chinedu Ekwunife
  */
object PulsarStreamProcessor {
  def main(args: Array[String]) {
    // Create the context with a 1 second batch size
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("pulsar-spark")
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val topic = STREAMING_TOPIC //"persistent://public/default/streaming-topic"

    val consumerConf: ConsumerConfigurationData[Array[Byte]] = new ConsumerConfigurationData[Array[Byte]]()

    consumerConf.setTopicNames(new util.HashSet[String](util.Arrays.asList(topic)))
    consumerConf.setSubscriptionName(STREAM_SUBSCRIPTION)
    consumerConf.setSubscriptionType(SubscriptionType.Exclusive)
    consumerConf.setSubscriptionInitialPosition(SubscriptionInitialPosition.Latest)
    //NOTE that setting a message listener implies that the ReceiverInputDStream[Array[Byte]] will contain no data to be acted upon as the data would already be consumed by message listener
    //consumerConf.setMessageListener(new MQListener[Array[Byte]])

    val serviceUrl = "pulsar://localhost:6650"

    val pulsarStream: ReceiverInputDStream[Array[Byte]] =
      ssc.receiverStream(new PulsarReceiver(StorageLevel.MEMORY_AND_DISK_2, serviceUrl, consumerConf, null))

    pulsarStream.foreachRDD(rdd => rdd.foreach(byteArray => println("Processed >>>>>>>>>>>>>>>>>>>>>>>> " + new String(byteArray))))

    ssc.start()
    ssc.awaitTermination()
  }
}
