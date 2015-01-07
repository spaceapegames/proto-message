package com.spaceape.protobuf.lang

import scala.beans.BeanProperty
import com.spaceape.protobuf.codegen.CodeGenContext

trait FieldType {
  def getLangType: String
  def getIsMessageType: Boolean = false
  def getIsRepeatableType: Boolean = false
  def getDefaultValue: String
  def getInputMethod: String = null
  def getOutputMethod: String = null
  def getFieldDescriptor: String
}

case class int32(getContext: () => CodeGenContext) extends FieldType {
  def getLangType: String = {
    getContext().mapper.typeMap("int32")
  }
  def getDefaultValue: String = "0"
  override def getInputMethod: String = getContext().mapper.methodMap("readInt32")
  override def getOutputMethod: String = getContext().mapper.methodMap("writeInt32")
  def getFieldDescriptor: String = "IntFieldDescriptor"
}

case class int64(getContext: () => CodeGenContext) extends FieldType {
  def getLangType: String = getContext().mapper.typeMap("int64")
  def getDefaultValue: String = "0"
  override def getInputMethod: String = getContext().mapper.methodMap("readInt64")
  override def getOutputMethod: String = getContext().mapper.methodMap("writeInt64")
  def getFieldDescriptor: String = "LongFieldDescriptor"
}

case class double(getContext: () => CodeGenContext) extends FieldType {
  def getLangType: String = getContext().mapper.typeMap("double")
  def getDefaultValue: String = "0.0"
  override def getInputMethod: String = getContext().mapper.methodMap("readDouble")
  override def getOutputMethod: String = getContext().mapper.methodMap("writeDouble")
  def getFieldDescriptor: String = "DoubleFieldDescriptor"
}

case class float(getContext: () => CodeGenContext) extends FieldType {
  def getLangType: String = getContext().mapper.typeMap("float")
  def getDefaultValue: String = "0"
  override def getInputMethod: String = getContext().mapper.methodMap("readFloat")
  override def getOutputMethod: String = getContext().mapper.methodMap("writeFloat")
  def getFieldDescriptor: String = "FloatFieldDescriptor"
}

case class string(getContext: () => CodeGenContext) extends FieldType {
  def getLangType: String = getContext().mapper.typeMap("string")
  def getDefaultValue: String = "null"
  override def getInputMethod: String = getContext().mapper.methodMap("readString")
  override def getOutputMethod: String = getContext().mapper.methodMap("writeString")
  def getFieldDescriptor: String = "StringFieldDescriptor"
}

case class bool(getContext: () => CodeGenContext) extends FieldType {
  def getLangType: String = getContext().mapper.typeMap("bool")
  def getDefaultValue: String = "false"
  override def getInputMethod: String = getContext().mapper.methodMap("readBool")
  override def getOutputMethod: String = getContext().mapper.methodMap("writeBool")
  def getFieldDescriptor: String = "BoolFieldDescriptor"
}

case class MessageType(name: String, @BeanProperty enumMsg: Boolean = false) extends FieldType {
  def getLangType: String = name
  def getDefaultValue: String = "null"
  override def getIsMessageType: Boolean = true
  def getFieldDescriptor: String = {
    if (enumMsg)
      "MessageEnumFieldDescriptor"
    else "MessageFieldDescriptor"
  }
}

case class Repeated(@BeanProperty repeatingType: FieldType, getContext: () => CodeGenContext) extends FieldType {
  def getLangType: String = getContext().mapper.typeMap("list").format(repeatingType.getLangType)
  def getDefaultValue: String = getContext().mapper.defaultValueMap("list").format(repeatingType.getLangType)
  override def getIsRepeatableType: Boolean = true
  def getFieldDescriptor: String = "ListFieldDescriptor"
}