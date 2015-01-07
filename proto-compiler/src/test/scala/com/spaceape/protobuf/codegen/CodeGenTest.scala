package com.spaceape.protobuf.codegen

import org.scalatest.ShouldMatchers
import org.testng.annotations.Test
import com.spaceape.protobuf.lang.ProtoContext
import org.stringtemplate.v4.{STRawGroupDir, ST, STGroupDir, STGroup}
import java.io.File

class CodeGenTest extends ShouldMatchers{
  val properties = Map[String, Any](
    "patch" -> true,
    "reflect" -> true
  )
  @Test
  def testJavaTemplate {
    val context = new ProtoContext
    context.loadingPaths = Set[String]("proto-java/src/test/resources/")
    context.loadProtoFile("model.proto")

    val codeGen = new CodeGen("proto-compiler/src/main/resources", "java", properties)
    codeGen.generate("model.proto", context, "proto-java/src/test/generatedJava")
  }

  @Test
  def testCSharpTemplate {
    val context = new ProtoContext
    context.loadingPaths = Set[String]("proto-java/src/test/resources/")
    context.loadProtoFile("model.proto")

    val codeGen = new CodeGen("proto-compiler/src/main/resources", "csharp", properties)
    codeGen.generate("model.proto", context, "proto-csharp/ProtoMessage/ProtoMessage/GeneratedCSharp")
  }
}
