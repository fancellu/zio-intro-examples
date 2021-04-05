package com.felstar.zio

import zio.clock.{Clock, sleep}
import zio.console.{Console, putStrLn}
import zio.duration.durationInt
import zio.{Exit, IO, Runtime, UIO, URIO}

object Fiber1 extends scala.App {

  // Notice we use scala.App, not zio.App, so have to get own runtime

  val runtime = Runtime.default

  // ZIO Fibonacci, made slower
  def slowfib(n: Long): URIO[Clock, Long] = for {
    v <- if (n <= 1) UIO.succeed(n)
         else slowfib(n - 1).zipWith(slowfib(n - 2))(_ + _)
    _ <- sleep(1.millis)
  } yield v

  // Spawn off 2 fibers, then zip them and await
  // As well as Console (for own use) we also need Clock, for slowfib
  val awaitFibers: URIO[Console with Clock, Exit[Nothing, (Long, Long)]] =
  for {
    fiber <- slowfib(10).fork
    _ <- putStrLn("Have forked")
    fiber2 <- slowfib(11).fork
    _ <- putStrLn("Have forked again")
    _ <- putStrLn("We are now waiting for both fibers to finish, as we made them slow")
    exit <- fiber.zip(fiber2).await
  } yield exit

  val exit: Exit[Nothing, (Long, Long)] = runtime.unsafeRun(awaitFibers)

  println(exit)

  // spawn off one fiber, but this time we just join
  val joinFiber: URIO[Console with Clock, String] = for {
    fiber <- IO.succeed("Hi!").fork
    _ <- putStrLn("Note how we have moved past fork")
    _ <- sleep(2.second)
    _ <- putStrLn("Now we join with the fiber")
    _ <- sleep(2.second)
    message <- fiber.join
  } yield message

  val greeting: String = runtime.unsafeRun(joinFiber)

  println(greeting)
}
