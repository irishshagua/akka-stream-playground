package com.mooneyserver.akkastreams.hierarchical.flows

import akka.NotUsed
import akka.stream.scaladsl.{Flow, GraphDSL, UnzipWith, ZipWith}
import akka.stream.{FanInShape3, FanOutShape3, FlowShape, Graph}
import com.mooneyserver.akkastreams.hierarchical.model.{Child, Grandparent, Parent}
import GraphDSL.Implicits._

object HierarchicalProcessingFlows {

  val handleGrandParent: Graph[FlowShape[Grandparent, Grandparent], NotUsed] =
    GraphDSL.create() { implicit builder =>
      val Input: FanOutShape3[Grandparent, Grandparent, Seq[Parent], Seq[Child]] = builder.add(UnzipWith(breakdownFamily))
      val GrandParent_Proc: FlowShape[Grandparent, Grandparent] = builder.add(Flow[Grandparent].map(processGrandParent))
      val Parent_Proc: FlowShape[Seq[Parent], Seq[Parent]] = builder.add(Flow[Seq[Parent]].map(processParents))
      val Child_Proc: FlowShape[Seq[Child], Seq[Child]] = builder.add(Flow[Seq[Child]].map(processChildren))
      val Rebuild_Grandparent: FanInShape3[Grandparent, Seq[Parent], Seq[Child], Grandparent] = builder.add(ZipWith[Grandparent, Seq[Parent], Seq[Child], Grandparent](recombineGrandParent))

      Input.out0 ~> GrandParent_Proc ~> Rebuild_Grandparent.in0
      Input.out1 ~> Parent_Proc ~> Rebuild_Grandparent.in1
      Input.out2 ~> Child_Proc ~> Rebuild_Grandparent.in2

      FlowShape(Input.in, Rebuild_Grandparent.out)
    }

  def breakdownFamily(grandparent: Grandparent): Tuple3[Grandparent, Seq[Parent], Seq[Child]] =
    (grandparent, grandparent.parents, grandparent.parents.flatMap(_.children))

  def processGrandParent(gp: Grandparent): Grandparent =
    gp.copy(name = s"${gp.name}-Modified")

  def processParents(p: Seq[Parent]): Seq[Parent] =
    for {parent <- p} yield parent.copy(name = s"${parent.name}-Modified")

  def processChildren(c: Seq[Child]): Seq[Child] =
    for {child <- c} yield child.copy(name = s"${child.name}-Modified")

  def recombineGrandParent(gp: Grandparent, p: Seq[Parent], c: Seq[Child]): Grandparent = {
    val parents = p map { parent =>
      val chillins = c filter { _.name.startsWith(parent.name.split("-Modified")(0))}
      parent.copy(children = chillins)
    }

    gp.copy(parents = parents)
  }
}
