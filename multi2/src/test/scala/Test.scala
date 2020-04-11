import org.scalatest.FunSuite

class Test extends FunSuite {

  test("multi2 can use common sub-project") {
    val entity = Entity("id", NestedEntity("value"))
  }
}
