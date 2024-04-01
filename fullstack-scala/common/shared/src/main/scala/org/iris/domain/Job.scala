package org.iris.domain

case class Job(
  company: String,
  title: String,
  description: String,
  externalUrl: String,
  salaryLo: Option[Int],
  salaryHi: Option[Int],
  currency: Option[String],
  remote: Boolean,
  location: String,
  country: Option[String]
)
object Job {
  val dummy = Job(
      "Iris",
      "Instructor",
      "Scala teacher",
      "org.iris",
      Some(0),
      Some(99),
      Some("CNY"),
      true,
      "Bucharest",
      Some("Russia")
    ) 
}
