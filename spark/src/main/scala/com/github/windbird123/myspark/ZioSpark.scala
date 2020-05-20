package com.github.windbird123.myspark

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.SparkSession
import zio._

object ZioSpark extends zio.App with LazyLogging {
  lazy val sparkLayer: ZLayer[Any, Throwable, Has[SparkSession]] = ZLayer.fromManaged {
    ZManaged.makeEffect(SparkSession.builder().master("local").appName("zio spark app").getOrCreate()) { spark =>
      logger.info("spark stopped")
      spark.stop()
    }
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val intRDD: ZIO[Has[SparkSession], Nothing, Int] = ZIO.access { env =>
      val spark = env.get[SparkSession]
      val out   = spark.sparkContext.parallelize(Seq("a", "bc", "def")).map(_.length).reduce(_ + _)
      logger.info(s"intRdd: $out ===============")
      out
    }

    intRDD.provideCustomLayer(sparkLayer).orDie
  }
}
