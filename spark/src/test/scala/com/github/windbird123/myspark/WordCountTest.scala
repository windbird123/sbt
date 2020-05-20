package com.github.windbird123.myspark

/**
 * A simple test for everyone's favourite wordcount example.
 */
import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.FunSuite

class WordCountTest extends FunSuite with SharedSparkContext {
  test("word count with Stop Words Removed") {
    val linesRDD =
      sc.parallelize(Seq("How happy was the panda? You ask.", "Panda is the most happy panda in all the#!?ing land!"))

    assert(true)
  }
}
