@main def hello(): Unit =
  println("Hello world!")
  println(msg)

def msg = "I was compiled by Scala 3. :)"
object Model:
  case class Actor(id: Int, firstName: String, lastName: String)