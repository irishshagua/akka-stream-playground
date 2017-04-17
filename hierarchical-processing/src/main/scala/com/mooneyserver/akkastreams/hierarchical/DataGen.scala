package com.mooneyserver.akkastreams.hierarchical

import com.mooneyserver.akkastreams.hierarchical.DataGen.DataSetSize
import com.mooneyserver.akkastreams.hierarchical.model.{Child, Grandparent, Parent}

import scala.util.Random

object DataGen {

  type DataSetSize = Int
}

trait DataGen {

  def modelProducer()(implicit dataSetSize: DataSetSize): Iterator[Grandparent] =
    (for { i <- 1 to dataSetSize} yield randomisedGrandparent(i)).toIterator

  private def randomisedGrandparent(id: Long): Grandparent = {
    val parents = for { i <- 1 to (Random.nextInt(4) + 1) } yield randomisedParent(id, i)
    Grandparent(
      name = s"GP-$id",
      parents = parents)
  }

  private def randomisedParent(gpId: Long, id: Int): Parent = {
    val children = for { i <- 1 to (Random.nextInt(4) + 1) } yield randomisedChild(gpId, id, i)
    Parent(
      name = s"GP-$gpId-P-$id",
      children = children)
  }

  private def randomisedChild(gpId: Long, pid: Int, id: Int): Child =
    Child(
      name = s"GP-$gpId-P-$pid-C-$id",
      onNaughtyList = Random.nextBoolean())
}
