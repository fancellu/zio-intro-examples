package com.felstar.zio

import zio._

object moduleConfig {
  type ModuleConfig = Has[ModuleConfig.Service]
  object ModuleConfig {
    trait Service {
      def configToString(config: Config): UIO[String]
    }

    val live: ULayer[Has[Service]] = ZLayer.succeed {
      new Service {
        def configToString(config: Config): UIO[String] = {
          UIO(s"Server: ${config.server}, port: ${config.port}")
        }
      }
    }
  }
  def configToString(config: Config): URIO[ModuleConfig,String]=
    ZIO.accessM(_.get.configToString(config))
}

// Module example. Remember, a Module is just a structured layer, useful for pluggable operations
object ModuleApp extends zio.App {

  import moduleConfig._

  import console._

  // 4 layer dependency
  val zio: URIO[Console with Has[String] with Has[Int] with ModuleConfig with Has[Config], Unit] = for {
    name <- ZIO.service[String] // same as but shorter than access[](._get)
    i <- ZIO.service[Int]
    config <- ZIO.service[Config]
    configString <- configToString(config)
    _    <- console.putStrLn(s"Hello, $name! $i $configString")
  } yield ()

  val nameLayer= ZLayer.succeed("John")
  val intLayer= ZLayer.succeed(456)
  val configLayer= ZLayer.succeed(Config("localhost",8080))

  def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val unit: UIO[Unit] =zio.provideLayer(Console.live ++ nameLayer ++ intLayer ++ ModuleConfig.live ++ configLayer)
    unit.as(ExitCode.success)
  }
}
