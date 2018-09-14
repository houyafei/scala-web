package com.nickname.app

import com.nickname.app.dto.Person
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write

trait TestJson {

  implicit val format = DefaultFormats

  val person = new Person(1,
    "houyafei",
    "spiderman",
    "he can do everything he wants")

  val jObject = write(person)

  val person2 = new Person(1,
    "summer",
    "batman",
    "he can do everything he wants")

  val jObject2 = write(person2)


}
