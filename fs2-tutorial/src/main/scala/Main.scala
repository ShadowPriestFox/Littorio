import Model.Actor
@main def hello(): Unit =
  println("Hello world!")
  println(msg)

def msg = "I was compiled by Scala 3. :)"
object Model:
  case class Actor(id: Int, firstName: String, lastName: String)

object Data:
  val henryCavil: Actor = Actor(0, "Henry", "Cavil")
  val galGodot: Actor = Actor(1, "Gal", "Godot")
  val ezraMiller: Actor = Actor(2, "Ezra", "Miller")
  val benFisher: Actor = Actor(3, "Ben", "Fisher")
  val rayHardy: Actor = Actor(4, "Ray", "Hardy")
  val jasonMomoa: Actor = Actor(5, "Jason", "Momoa")
  val scarlettJohansson: Actor = Actor(6, "Scarlett", "Johansson")