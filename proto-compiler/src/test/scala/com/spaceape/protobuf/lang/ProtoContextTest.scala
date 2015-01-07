package com.spaceape.protobuf.lang

import org.scalatest.ShouldMatchers
import org.testng.annotations.Test

class ProtoContextTest extends ShouldMatchers{
  @Test
  def testImportCircle {
    val context = new ProtoContext
    context.loadingPaths = Set[String]("proto-java/src/test/resources/")
    context.loadProtoFile("model.proto")
  }
}
