package com.cienet.akkacluster

import akka.actor.{ActorPath, ActorSystem}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import com.cienet.akkacluster.Main.args
import com.typesafe.config.ConfigFactory

object OutsideClient extends App {
  var config = ConfigFactory.parseString(
    """
      akka {
        remote { #//<co id="remote_config"/>
          use-passive-connections=off
          enabled-transports = ["akka.remote.netty.tcp"]
          log-remote-lifecycle-events = off
          netty.tcp {
            hostname = "127.0.0.1"
          }
        }
        cluster {
          seed-nodes = [
            "akka.tcp://OutsideClient@127.0.0.1:2553"]
            auto-down-unreachable-after = 10s
        }
        actor {
          provider = "akka.cluster.ClusterActorRefProvider" #//<co id="cluster_arp"/>
        }
        extensions = ["akka.cluster.client.ClusterClientReceptionist"]
      }
    """.stripMargin)

  var port = 2553

  config = config.withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port"))
  config = config.withFallback(ConfigFactory.parseString(s"akka.cluster.client.receptionist.name=receptionist-$port"))
  config = config.withFallback(ConfigFactory.load())

  val remoteSystemName = "ClusterTestSystem"
  val systemName = "OutsideClient"

  val system = ActorSystem(systemName, config)

  val initialContacts = Set(
    ActorPath.fromString(s"akka.tcp://$remoteSystemName@127.0.0.1:2551/system/receptionist-2551"),
    ActorPath.fromString(s"akka.tcp://$remoteSystemName@127.0.0.1:2552/system/receptionist-2552"))
  val settings = ClusterClientSettings(system).withInitialContacts(initialContacts)
  //val settings = ClusterClientSettings(system)
  val client = system.actorOf(ClusterClient.props(settings), "client")
  Thread.sleep(2000)
  for (i <- 1 to 10) {
    client ! ClusterClient.SendToAll(s"/user/TestActor-$i", "hi")
  }
  println("All message sent.")
}
