package com.cienet.akkacluster

import akka.actor.{ActorPath, ActorSystem, Address, AddressFromURIString, Props}
import akka.cluster.Cluster
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import com.typesafe.config.ConfigFactory
import akka.actor.typed.receptionist.ServiceKey
import akka.cluster.client.{ClusterClient, ClusterClientReceptionist, ClusterClientSettings}

object Main extends App {

  var config = ConfigFactory.parseString(
    """
      akka {
        remote { #//<co id="remote_config"/>
          use-passive-connections = off
          enabled-transports = ["akka.remote.netty.tcp"]
          log-remote-lifecycle-events = off
          netty.tcp {
            hostname = "127.0.0.1"
          }
        }
        actor {
          provider = "akka.cluster.ClusterActorRefProvider" #//<co id="cluster_arp"/>
        }
        extensions = ["akka.cluster.client.ClusterClientReceptionist"]
      }
    """.stripMargin)

  var localPort = 2551
  if (args.size > 0) {
    localPort = 2552
  }
  config = config.withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$localPort"))
  config = config.withFallback(ConfigFactory.parseString(s"akka.cluster.client.receptionist.name=receptionist-$localPort"))
  config = config.withFallback(ConfigFactory.load())

  val systemName = "ClusterTestSystem"

  val system = ActorSystem(systemName, config)

  val cluster = Cluster(system)
  val list: List[Address] = List(
    AddressFromURIString(s"akka.tcp://$systemName@127.0.0.1:2551"),
    new Address("akka.tcp", systemName, "127.0.0.1", 2552)
  )
  cluster.joinSeedNodes(list)

  println(s"Starting node with roles: ${Cluster(system).selfRoles}")

  val simpleClusterListener = system.actorOf(Props[SimpleClusterListener], name = "SimpleClusterListener")

  val testActorKey = ServiceKey[TestActor]("TestActor")

  val mode = scala.io.StdIn.readLine()
  if (mode == "1") {
    for (i <- 1 to 10) {
      val actor = system.actorOf(
        ClusterRouterPool(BroadcastPool(10), ClusterRouterPoolSettings(
          totalInstances = 100, maxInstancesPerNode = 20,
          allowLocalRoutees = false)).props(Props[TestActor]), s"TestActor-$i")
      ClusterClientReceptionist(system).registerService(actor)
    }
    println("All Actor done.")
  } else {
    val initialContacts = Set(
      ActorPath.fromString(s"akka.tcp://$systemName@127.0.0.1:2551/system/receptionist-2551"),
      ActorPath.fromString(s"akka.tcp://$systemName@127.0.0.1:2552/system/receptionist-2552"))
    val settings = ClusterClientSettings(system).withInitialContacts(initialContacts)
    //val settings = ClusterClientSettings(system)
    val client = system.actorOf(ClusterClient.props(settings), "client")
    Thread.sleep(2000)
    for (i <- 1 to 10) {
      client ! ClusterClient.SendToAll(s"/user/TestActor-$i", "hi")
    }
    println("All message sent.")
  }
}
