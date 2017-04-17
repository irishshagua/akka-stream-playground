package com.mooneyserver.akkastreams.hierarchical

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait Akka {
  implicit val actorSystem: ActorSystem = ActorSystem("Stream-Hierarchical-Processing")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
}
