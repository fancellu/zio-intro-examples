package com.felstar.zio

import zio.{Runtime, UIO, URIO, ZIO}

// Simple example of providing an Env inside a scala.App
object ScalaApp extends scala.App {

  // we expect Config as our env
  // so Config is seen as a (type) parameter to this Unfailing IO with an Environment
  val configToString: URIO[Config, String] =
  for {
    server <- ZIO.access[Config](_.server)
    port <- ZIO.access[Config](_.port)
  } yield s"Server: $server, port: $port"

  val configToSomething: URIO[Config, String] =
    for {
      something <- ZIO.access[Config](_.something)
    } yield s"$something"

  val runtime = Runtime.default

  val enriched: UIO[String] = configToString.provide(Config("server", 8080, "hello!"))

  val string = runtime.unsafeRunTask(enriched)

  println(string)

  val bothStrings: URIO[Config, (String, String)] =
    for {
      st1 <- configToString
      st2 <- configToSomething
    } yield (st1, st2)

  val tuple2: UIO[(String, String)] = bothStrings.provide(Config("server2", 8081, "byeee!"))

  println(runtime.unsafeRunTask(tuple2))

  // if you wanted more than just 1 Env, this is what Layers/Modules are for
}
