package com.projet
import org.apache.spark._

object Main {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setAppName("spark-app")
      .setMaster("local[*]")
      .set("spark.driver.host","127.0.0.1")

    val sc = new SparkContext(conf)

    sc.parallelize(List("a","b","c"))
      .foreach(println)





  }

}