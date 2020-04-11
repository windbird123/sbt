object Main extends App {
  println("multi2 can use common sub-project")

  val entity: Entity = Entity("id", NestedEntity("value"))

  println("multi2 can use pureconfig dependency")
}
