package com.github.windbird123.myspark

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.SparkSession
import zio._

object ZioSpark extends zio.App with LazyLogging {

  lazy val sparkLayer: ZLayer[Any, Nothing, Has[SparkSession]] = ZLayer.fromManaged {
    val acquire: UIO[SparkSession] =
      UIO.effectTotal(SparkSession.builder().master("local").appName("zio spark app").getOrCreate())
    ZManaged.make(acquire){spark =>
      logger.info("spark stopped")
      UIO.effectTotal(spark.stop())
    }
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val intRDD: ZIO[Has[SparkSession], Nothing, Int] = ZIO.access { env =>
      val spark = env.get[SparkSession]
      val out   = spark.sparkContext.parallelize(Seq("a", "bc", "def")).map(_.length).reduce(_ + _)
      logger.info(s"intRdd: $out ===============")
      out
    }
    intRDD.provideLayer(sparkLayer)
  }
}
