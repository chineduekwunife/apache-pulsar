package com.pulsar.spark.batch

import org.apache.spark.sql.SparkSession

/**
  * Simple batch Spark Job which reads lines in a README.md file and outputs the number of lines containing "a" and "b"
  *
  * Before testing, change logFile to match the path to README.md in your system
  *
  * To test:
  *  1. Install spark - see https://www.tutorialkart.com/apache-spark/how-to-install-spark-on-mac-os/
  *
  *  2. Build and submit the Spark job
  * ./gradlew services:spark:clean
  * ./gradlew services:spark:shadowJar
  * /usr/local/Cellar/apache-spark/2.4.4/bin/spark-submit --class com.pulsar.spark.batch.FileLineProcessor --master local services/spark/build/libs/spark-1.0.0-SNAPSHOT-all.jar
  *
  * @author Chinedu Ekwunife
  */
object FileLineProcessor {
  def main(args: Array[String]) {
    val logFile = "/Users/chineduekwunife/Projects/java/pulsar/README.md"
    val spark = SparkSession.builder.appName("File Line Processor Application").getOrCreate()
    val logData = spark.read.textFile(logFile).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()

    println(s"Lines with a: $numAs, Lines with b: $numBs")

    spark.stop()
  }
}
