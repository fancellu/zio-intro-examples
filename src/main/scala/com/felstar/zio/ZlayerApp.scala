package com.felstar.zio

import zio._
import console._

// layers example, with no modules, using zio.App
object ZLayerApp extends zio.App {

  // 1 layer dependency, returns a String
  val configToString: URIO[Has[Config], String] =
    for {
      server <- ZIO.access[Has[Config]](_.get.server)
      port   <- ZIO.access[Has[Config]](_.get.port)
    } yield s"Server: $server, port: $port"

  // 4 layer dependency, returns Unit
  val zio: URIO[Console with Has[String] with Has[Int] with Has[Config], Unit] = for {
    name <- ZIO.service[String] // same as access[Has[String]](._get)
    i <- ZIO.service[Int]
    configString <- configToString
    // the long way
    // configString <- ZIO.accessM(configToString.provide)
    _    <- console.putStrLn(s"Hello, $name! Int is $i config is $configString")
  } yield ()

    // creating 3 layers for String, Int, and Config
  val nameLayer= ZLayer.succeed("Dino")
  val intLayer= ZLayer.succeed(123)
  val configLayer= ZLayer.succeed(Config("host",8888))

  def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val unit: UIO[Unit] =zio.provideLayer(Console.live ++ nameLayer ++ intLayer ++ configLayer )
    unit.as(ExitCode.success)
  }
}
