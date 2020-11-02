object Main extends App {
  println("multi1 can use common sub-project")

  val entity: Entity = Entity("id", NestedEntity("value"))

  println("multi1 can use monocle dependency")
}
