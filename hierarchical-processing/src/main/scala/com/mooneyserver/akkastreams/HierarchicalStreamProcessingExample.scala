package com.mooneyserver.akkastreams

import akka.stream.scaladsl.{Sink, Source}
import com.mooneyserver.akkastreams.hierarchical.DataGen.DataSetSize
import com.mooneyserver.akkastreams.hierarchical.model.Grandparent
import com.mooneyserver.akkastreams.hierarchical.{Akka, DataGen}

import com.mooneyserver.akkastreams.hierarchical.flows.HierarchicalProcessingFlows.handleGrandParent

object HierarchicalStreamProcessingExample extends App
  with DataGen with Akka {

  implicit val dataSetSize: DataSetSize = 10

  Source.fromIterator[Grandparent](modelProducer)
    .via(handleGrandParent)
    .runWith(Sink.foreach(println))
}
