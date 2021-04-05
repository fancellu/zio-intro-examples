package com.felstar.zio

import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, assert}
import zio.json._

object Json1Spec extends DefaultRunnableSpec {
  val decodeSuite = suite("decodeSuite")(
    test("fromJson decodes a black cat") {
      val either = """{ "colour": "black" }""".fromJson[Cat]
      assert(either)(equalTo(Right(Cat("tiddles", "black"))))
    },
    test("fromJson fails on cat with no colour") {
      val either = """{ "whatisthis": 0.5 }""".fromJson[Cat]
      assert(either)(equalTo(Left(".colour(missing)")))
    },
    test("fromJson decodes a marmalade cat from disambiguated Animal") {
      val either = """{ "Cat":{ "colour":"marmalade" }}""".fromJson[Animal]
      assert(either)(equalTo(Right(Cat("tiddles", "marmalade"))))
    },
    test("fromJson decodes a max the dog from disambiguated Animal") {
      val either = """{ "Dog": { "name": "max", "age": 12 }}""".fromJson[Animal]
      assert(either)(equalTo(Right(Dog("max", 12))))
    },
    test("fromJson fails on unknown Animal type") {
      val either = """{ "Dog2": { "age": 8 }}""".fromJson[Animal]
      assert(either)(equalTo(Left("(invalid disambiguator)")))
    },
    test("fromJson decodes a RawNum") {
      val either = "123".fromJson[RawNum]
      assert(either)(equalTo(Right(RawNum(123))))
    },
    test("fromJson fails on non numeric RawNum") {
      val either = "quack".fromJson[RawNum]
      assert(either)(equalTo(Left("(expected a number, got q)")))
    }
  )

  val encodeSuite = suite("encodeSuite")(
    test("toJson encodes a cat") {
      val string = Cat("daisy", "tabby").toJson
      assert(string)(equalTo("""{"name":"daisy","colour":"tabby"}"""))
    },
    test("toJson encodes a dog") {
      val string = Dog("spot", 11).toJson
      assert(string)(equalTo("""{"name":"spot","age":11}"""))
    },
    test("toJson encodes a dog as Animal") {
      val string = (Dog("fido", 3): Animal).toJson
      assert(string)(equalTo("""{"Dog":{"name":"fido","age":3}}"""))
    },
    test("toJson encodes a RawNum") {
      val string = RawNum(456).toJson
      assert(string)(equalTo("456"))
    }
  )

  // multiple suites, as suites can contain suites
  def spec = suite("Json1Spec")(decodeSuite, encodeSuite)
}
