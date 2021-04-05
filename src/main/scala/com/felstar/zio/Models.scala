package com.felstar.zio
import zio.json.{DeriveJsonCodec, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

final case class Config(server: String, port: Int, something: String = "")

sealed trait Animal {
  val name: String
}

case class Cat(name: String = "tiddles", colour: String) extends Animal

case class Dog(name: String, age: Int) extends Animal

case class RawNum(value: Int)

object Cat {
  //implicit val decoder = DeriveJsonDecoder.gen[Cat]
  //implicit val encoder = DeriveJsonEncoder.gen[Cat]
  implicit val codec = DeriveJsonCodec.gen[Cat]
}

object Dog {
  implicit val encoder = DeriveJsonEncoder.gen[Dog]
}

object Animal {
  // implicit val decoder= DeriveJsonDecoder.gen[Animal]
  // implicit val encoder= DeriveJsonEncoder.gen[Animal]
  implicit val codec = DeriveJsonCodec.gen[Animal]
}

object RawNum {
  implicit val decoder: JsonDecoder[RawNum] =
    JsonDecoder[Int].map(RawNum(_))
  implicit val encoder: JsonEncoder[RawNum] =
    JsonEncoder[Int].contramap(_.value)
}

