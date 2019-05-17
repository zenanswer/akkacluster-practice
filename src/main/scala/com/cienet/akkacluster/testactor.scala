package com.cienet.akkacluster

import akka.actor.{Actor, ActorLogging}


class TestActor extends Actor with ActorLogging {
  def receive = {
    case message: String =>
      log.info("{}, Got one message:{}", self.toString(), message)
    case _ =>
      log.info("Got some thing.")
  }
}
