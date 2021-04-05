package com.felstar.zio

import zio._
import zio.test.Assertion._
import zio.test._

import moduleConfig._

object ModuleConfigSpec extends DefaultRunnableSpec {
  def spec = suite("ModuleConfigSpec")(
    testM("configToString correctly returns string") {
      for {
        configString <- configToString(Config("local", 8888))
      } yield assert(configString)(equalTo("Server: local, port: 8888"))
    }
  ).provideCustomLayer(ModuleConfig.live) // this is how we inject an impl for ModuleConfig
}