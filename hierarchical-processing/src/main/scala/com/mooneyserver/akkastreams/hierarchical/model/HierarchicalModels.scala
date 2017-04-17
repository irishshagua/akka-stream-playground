package com.mooneyserver.akkastreams.hierarchical.model

case class Grandparent(name: String, parents: Seq[Parent])

case class Parent(name: String, children: Seq[Child])

case class Child(name: String, onNaughtyList: Boolean)
