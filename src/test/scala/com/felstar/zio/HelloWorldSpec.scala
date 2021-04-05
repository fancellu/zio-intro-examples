package com.felstar.zio

import com.felstar.zio.HelloWorld.{sayHello, sayHelloRandom, sayHelloSlow}
import zio._
import zio.clock.Clock
import zio.console._
import zio.duration.durationInt
import zio.random.Random
import zio.test.Assertion._
import zio.test._
import zio.test.environment._


object HelloWorld {
  val sayHello: URIO[Console, Unit] =
    console.putStrLn("Hello, World!")

  val sayHelloRandom: URIO[Console with Random, Int] = {
    console.putStrLn("Hello") *> console.putStrLn("World!")  *> random.nextInt

  }

  val sayHelloSlow: URIO[Console with Clock, Unit] = {
     clock.sleep(10.seconds) *> console.putStrLn("Hello")
  }
}

object HelloWorldSpec extends DefaultRunnableSpec {
  def spec = suite("HelloWorldSpec")(
    testM("sayHello correctly writes to console") {
      for {
        _      <- sayHello
        output <- TestConsole.output
      } yield assert(output)(equalTo(Vector("Hello, World!\n")))
    },
    testM("sayHelloRandom correctly writes to console and returns random Int") {
        for {
            // we set TestRandom seed, so we get the same Int back, each time
            // _  <- TestRandom.setSeed(999)
            // or we can feed our own values here
          _  <- TestRandom.feedInts(555)
          myInt  <- sayHelloRandom
          output <- TestConsole.output
        } yield assert(myInt,output)(equalTo(555,Vector("Hello\n","World!\n")))
      },
    testM("sayHelloSlow correctly writes to console and doesn't wait 10 seconds") {
      for {
          // we have to fork, so we can move on the TestClock
          // TestClock is not the live realtime clock, and is manual adjusted
        sayHelloFiber <- sayHelloSlow.fork
        _ <- TestClock.adjust(10.seconds)
          // we wait here for fiber to end
        _ <- sayHelloFiber.join
        output <- TestConsole.output
      } yield assert(output)(equalTo(Vector("Hello\n")))
    },
    testM("sayHelloSlow doesn't get to write to console when interrupted") {
      for {
        sayHelloFiber <- sayHelloSlow.fork
        _ <- sayHelloFiber.interrupt
        output <- TestConsole.output
      } yield assert(output)(isEmpty)
    }
  )
}