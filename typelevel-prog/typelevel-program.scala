import io.circe.* 
import io.circe.generic.semiauto
import cats.implicits.*

case class Phone(number: String, prefix: Int)
case class Email(primary: String, secondary: Option[String])
case class Address(country: String, city: String)
case class User(email: Email,phone: Phone, address: Address)

object Email:
  given Codec.AsObject[Email] = semiauto.deriveCodec[Email]

object Phone:
  given Codec.AsObject[Phone] = semiauto.deriveCodec[Phone]

val user = User(
  Email(
    "hxx@iris.org",
    "hhj@iris.org".some
  ),
  Phone("555-333-4444", 86),
  Address("Ca", "Laval")
)

