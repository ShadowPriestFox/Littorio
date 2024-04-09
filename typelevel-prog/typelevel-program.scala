import io.circe.* 
import io.circe.generic.semiauto
import cats.implicits.*
import scala.compiletime.summonInline

case class Phone(number: String, prefix: Int)
case class Email(primary: String, secondary: Option[String])
case class Address(country: String, city: String)
case class User(email: Email,phone: Phone, address: Address)

object Email:
  given Codec.AsObject[Email] = semiauto.deriveCodec[Email]

object Phone:
  given Codec.AsObject[Phone] = semiauto.deriveCodec[Phone]

object Address:
  given Codec.AsObject[Address] = semiauto.deriveCodec[Address]

val user = User(
  Email(
    "hxx@iris.org",
    "hhj@iris.org".some
  ),
  Phone("555-333-4444", 86),
  Address("Ca", "Laval")
)

def listToJson[A: Encoder](ls: List[A]): List[Json] = 
  ls match
    case Nil => Nil
    case h :: t => 
      val encoder = summon[Encoder[A]]
      val json = encoder(h)
      json :: listToJson(t)

inline def tupleToJson(tuple: Tuple): List[JsonObject] =
  inline tuple match
    case EmptyTuple => Nil
    case tup: (h *: t) => 
      val encoder = summonInline[Encoder.AsObject[h]]
      val json = encoder.encodeObject(tup.head)
      json :: tupleToJson(tup.tail)

def concatObjects(jsons: List[JsonObject]): Json = Json.obj(jsons.flatMap(_.toList): _*)
val encoder = Encoder.instance[User]: value =>
  val fields = Tuple.fromProductTyped(value)
  val jsons = tupleToJson(fields)
  concatObjects(jsons)