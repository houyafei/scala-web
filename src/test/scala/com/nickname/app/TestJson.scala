package com.nickname.app

import java.util.UUID

import com.nickname.app.dto.Person
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write

trait TestJson {

  implicit val format = DefaultFormats

  val person = new Person(UUID.randomUUID().hashCode(),
    "houyafei",
    "spiderman",
    "he can do everything he wants")

  val jObject = write(person)

  val person1 = new Person(UUID.randomUUID().hashCode(),
    "summer",
    "batman",
    "he can do everything he wants")

  val jObject1 = write(person1)

  val person2 = new Person(UUID.randomUUID().hashCode(),
    "summer",
    "batman",
    "he can do everything he wants")

  val jObject2 = write(person2)


}
