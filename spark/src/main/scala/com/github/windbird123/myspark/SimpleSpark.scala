package com.github.windbird123.myspark

import org.apache.spark.sql.SparkSession
import zio._
import zio.duration._

object SimpleSpark {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("zio spark app").getOrCreate()

    val rdd = spark.sparkContext.parallelize(Seq("a", "bc", "def"))

    lazy val util = zio.Runtime.default.unsafeRun(AddrUtil.create().provideCustomLayer(AddressDiscovery.live))
    val trr =
      rdd.map { x =>
        val y = for {
          c <- util.choose()
          _ <- ZIO.sleep(1.seconds)
        } yield x + c
        zio.Runtime.default.unsafeRun(y.provideCustomLayer(AddressDiscovery.live))
      }

    trr.foreach(x => println(x))

    spark.stop()
  }
}
