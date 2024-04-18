import Model.Actor
import fs2.Stream
import fs2.Pure
import Data.*
@main def hello(): Unit =
  val jlActors: Stream[Pure, Actor] = Stream(
    henryCavil,
    galGodot,
    ezraMiller,
    benFisher,
    rayHardy,
    jasonMomoa
  )
  val tomHollandStream: Stream[Pure, Actor] = Stream.emit(tomHolland)
  val jlActorList: List[Actor] = jlActors.toList
  val jlActorVector: Vector[Actor] = jlActors.toVector
  val infiniteJLActors: Stream[Pure, Actor] = jlActors.repeat

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
  val robertDowneyJr: Actor = Actor(7,"Robert", "Downey Jr.")
  val chrisEvans: Actor = Actor(8,"Chris", "Evans")
  val markRuffalo: Actor = Actor(9, "Mark", "Ruffalo")
  val chrisHemsworth: Actor = Actor(10, "Chris", "Hemsworth")
  val jeremyRenner: Actor = Actor(11, "Jeremy", "Renner")
  val tomHolland: Actor = Actor(12, "Tom", "Holland")
  val tobeyMaguire: Actor = Actor(13, "Tobey", "Maguire")
  val andrewGarfield: Actor = Actor(14, "Anndrew", "Garfield")
