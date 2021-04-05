package com.felstar.zio

import zio.{App, ExitCode, URIO, console}
import zio.json._

object Json1 extends App{

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    (
        // decoding
      console.putStrLn("""{ "colour": "black" }""".fromJson[Cat].toString) *>
      console.putStrLn("""{ "whatisthis": 0.5 }""".fromJson[Cat].toString) *>
        // We must present a known disambiguator
      console.putStrLn("""{ "Cat":{ "colour":"marmalade" }}""".fromJson[Animal].toString) *>
      console.putStrLn("""{ "Dog": { "name": "max", "age": 12 }}""".fromJson[Animal].toString) *>
      console.putStrLn("""{ "Dog2": { "age": 8 }}""".fromJson[Animal].toString) *>
      console.putStrLn("123".fromJson[RawNum].toString) *>
      console.putStrLn("quack".fromJson[RawNum].toString) *>
        // encoding
      console.putStrLn(Cat("daisy","tabby").toJson) *>
      console.putStrLn(Dog("spot",11).toJson) *>
      console.putStrLn((Dog("fido",3):Animal).toJson) *>
      console.putStrLn(RawNum(456).toJson)
    ).exitCode
  }
}
