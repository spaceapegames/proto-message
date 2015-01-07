package com.spaceape.protobuf.codegen

trait TypeMapper {
  val typeMap: Map[String, String]
  val defaultValueMap: Map[String, String]
  val filePostfix: String
  val templateMap: Map[String, String]
  val methodMap: Map[String, String]
}

object TypeMappers {
  val mappers = Map(
    "java" -> JavaTypeMapper,
    "csharp" -> CSharpTypeMapper
  )
}

case object JavaTypeMapper extends TypeMapper {
  val typeMap = Map (
    "int32" -> "int",
    "int64" -> "long",
    "double" -> "double",
    "float" -> "float",
    "string" -> "String",
    "bool" -> "boolean",
    "list" -> "List<%s>"
  )
  val defaultValueMap = Map (
    "list" -> "new ArrayList<%s>()"
  )

  val filePostfix: String = ".java"

  val templateMap = Map (
    "ProtoMessage" -> "JavaMessageTemplate",
    "ProtoEnumMessage" -> "JavaEnumTemplate",
    "ClassRegistry" -> "JavaClassRegistry"
  )

  val methodMap = Map (
    "readInt32" -> "readInt32",
    "writeInt32" -> "writeInt32",
    "readInt64" -> "readInt64",
    "writeInt64" -> "writeInt64",
    "readDouble" -> "readDouble",
    "writeDouble" -> "writeDouble",
    "readFloat" -> "readFloat",
    "writeFloat" -> "writeFloat",
    "readString" -> "readString",
    "writeString" -> "writeString",
    "readBool" -> "readBool",
    "writeBool" -> "writeBool"
  )
}

case object CSharpTypeMapper extends TypeMapper {
  val typeMap = Map (
    "int32" -> "int",
    "int64" -> "long",
    "double" -> "double",
    "float" -> "float",
    "string" -> "string",
    "bool" -> "Boolean",
    "list" -> "List<%s>"
  )
  val defaultValueMap = Map (
    "list" -> "new List<%s>()"
  )

  val filePostfix: String = ".cs"

  val templateMap = Map (
    "ProtoMessage" -> "CSharpMessageTemplate",
    "ProtoEnumMessage" -> "CSharpEnumTemplate",
    "ClassRegistry" -> "CSharpClassRegistry"
  )

  val methodMap = Map (
    "readInt32" -> "ReadInt32",
    "writeInt32" -> "WriteInt32",
    "readInt64" -> "ReadInt64",
    "writeInt64" -> "WriteInt64",
    "readDouble" -> "ReadDouble",
    "writeDouble" -> "WriteDouble",
    "readFloat" -> "ReadFloat",
    "writeFloat" -> "WriteFloat",
    "readString" -> "ReadString",
    "writeString" -> "WriteString",
    "readBool" -> "ReadBool",
    "writeBool" -> "WriteBool"
  )
}