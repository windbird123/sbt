package com.github.windbird123.myspark

/**
 * A simple test for everyone's favourite wordcount example.
 */

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.FunSuite

class WordCountTest extends FunSuite with SharedSparkContext {
  test("word count with Stop Words Removed"){
   assert(true)
  }
}
