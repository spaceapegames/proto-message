package com.spaceape.protobuf.lang

import java.lang.reflect.{InvocationTargetException, Method}
import scala.beans.BeanProperty
import com.spaceape.protobuf.codegen.CodeGenContext

object ProtoFile {
  var keywords = Map.empty[String, Method]
  var supportedTypes = Map(
    "int32" -> int32,
    "int64" -> int64,
    "double" -> double,
    "float" -> float,
    "string" -> string,
    "bool" -> bool
  )

  registerKeyWord("import", "importSymbol")
  registerKeyWord("package", "packageSymbol")
  registerKeyWord("message", "message")
  registerKeyWord("extends", "classExtends")
  registerKeyWord("{", "bracketStart")
  registerKeyWord("}", "bracketEnd")
  registerKeyWord("required", "required")
  registerKeyWord("optional", "optional")
  registerKeyWord("repeated", "repeated")
  registerKeyWord("//", "lineComment")
  registerKeyWord("enum", "enumType")

  private def registerKeyWord (word: String, methodName: String){
    classOf[ProtoFile].getDeclaredMethods.find(_.getName == methodName) match {
      case Some(method) => keywords += (word -> method)
      case None => throw new IllegalArgumentException("method %s is not found".format(methodName))
    }
  }

  def parse(content: String, context: ProtoContext, protoFile: ProtoFile): ProtoFile = {
    var words: StreamedWords = new StreamedArrayWords(content)

    var word: Option[String] = words.readWord
    while (word.isDefined){
      val keyword = word.get
      if (!keywords.contains(keyword)){
        throw new ProtoFileParsingException("%s is not a keyword".format(keyword), words.lineNumber)
      }
      val method = keywords(keyword)
      try {
        words = method.invoke(protoFile, words).asInstanceOf[StreamedWords]
      }catch {
        case e: InvocationTargetException =>
          throw e.getCause
      }
      word = words.readWord
    }
    protoFile.importedFiles.foreach{
      filename =>
        context.loadProtoFile(filename)
        val loadedFile = context.loadedFiles(filename)
        protoFile.importedPacks += (filename -> loadedFile.packageName)
    }

    //all visible messages
    protoFile.validateMessages(ScopedMessages(protoFile, context))
    protoFile
  }
}
class ProtoFile(val filename: Option[String] = None) {
  var packageName: Option[String] = None
  var importedFiles = Set.empty[String]
  var importedPacks = Map.empty[String, Option[String]] // file/package mapping
  var messages = Map.empty[String, ProtoType]

  var codeGenContext: CodeGenContext = null

  var currentMessage: Option[ProtoType] = None
  var messageBodyStarted = false

  def getCodeGenContext: () => CodeGenContext = () => codeGenContext
  def packageSymbol(words: StreamedWords) = {
    if (currentMessage.isDefined) throw new ProtoFileParsingException("package must be defined before message", words.lineNumber)
    if (packageName.isDefined) throw new ProtoFileParsingException("package has been defined as %s".format(packageName.get), words.lineNumber)

    packageName = Some(words.readWord.getOrElse(throw new ProtoFileParsingException("no package name is provided", words.lineNumber)))
    words
  }
  def importSymbol(words: StreamedWords) = {
    if (currentMessage.isDefined) throw new ProtoFileParsingException("import must be defined before message", words.lineNumber)
    importedFiles += words.readWord.getOrElse(throw new ProtoFileParsingException("no import file name is provided", words.lineNumber))
    words
  }
  def message(words: StreamedWords) = {
    if (currentMessage.isDefined) throw new ProtoFileParsingException("can't define a message if another message definition is not completed", words.lineNumber)
    if (messageBodyStarted) throw new ProtoFileParsingException("can't define a message if another message definition is started", words.lineNumber)

    words.readWord match {
      case Some(name) =>
        if (messages.contains(name)){
          throw new ProtoFileParsingException("message %s is defined twice".format(name), words.lineNumber)
        }
        val msgDef = new ProtoMessage(name, packageName)
        currentMessage = Some(msgDef)
        messages += (name -> msgDef)
      case None =>
        throw new ProtoFileParsingException("message missed name", words.lineNumber)
    }
    words
  }

  def classExtends (words: StreamedWords) = {
    if (currentMessage.isEmpty) throw new ProtoFileParsingException("can't extend without a message definition", words.lineNumber)
    if (messageBodyStarted) throw new ProtoFileParsingException("can't define a extension if a message definition is started", words.lineNumber)
    if (currentMessage.get.superClass.isDefined) throw new ProtoFileParsingException("can't define a extension when there is an existing extend on the same message", words.lineNumber)

    words.readWord match {
      case Some(name) =>
        currentMessage.get.superClass = Some(name)
      case None =>
        throw new ProtoFileParsingException("extends missed name on message %s".format(currentMessage.get.name), words.lineNumber)
    }
    words
  }

  def enumType (words: StreamedWords) = {
    if (currentMessage.isDefined) throw new ProtoFileParsingException("can't define an enum if a message definition is not completed", words.lineNumber)
    if (messageBodyStarted) throw new ProtoFileParsingException("can't define an enum if a message definition is started", words.lineNumber)

    val enum = new ProtoEnumMessage(words.readWord.getOrElse(throw new ProtoFileParsingException("missing enum name", words.lineNumber)), packageName)
    if (words.readWord.getOrElse("") != "{") {
      throw new ProtoFileParsingException("expect { to start enum %s's body".format(enum.name), words.lineNumber)
    }
    var startWord = words.readWord
    while (startWord.getOrElse(throw new ProtoFileParsingException("expected } to end enum "+enum.name, words.lineNumber)) != "}"){
      if (words.readWord.getOrElse("") != "=") {
        throw new ProtoFileParsingException("expect = to specify enum %s's field number".format(enum.name), words.lineNumber)
      }
      val fieldNumber = words.readWord.getOrElse(throw new ProtoFileParsingException("missing enum field number", words.lineNumber)).toInt
      enum.fields += (startWord.get -> ProtoTypeEnumField(startWord.get, fieldNumber))
      startWord = words.readWord
    }

    messages += (enum.name -> enum)

    words
  }

  def bracketStart(words: StreamedWords): StreamedWords = {
    if (currentMessage.isEmpty) throw new ProtoFileParsingException("can't start a message body when there is no message definition", words.lineNumber)
    if (messageBodyStarted) throw new ProtoFileParsingException("can't start a message body if it's already started", words.lineNumber)

    messageBodyStarted = true
    words
  }

  def bracketEnd (words: StreamedWords): StreamedWords = {
    if (currentMessage.isEmpty) throw new ProtoFileParsingException("can't end a message body when there is no message definition", words.lineNumber)
    if (!messageBodyStarted) throw new ProtoFileParsingException("can't end a message body if it's been started", words.lineNumber)

    currentMessage.foreach {
      msg =>
        messages += (msg.name -> msg)
    }

    currentMessage = None
    messageBodyStarted = false
    words
  }

  def required (words: StreamedWords) = {
    addField(words, true)
  }

  def optional (words: StreamedWords) = {
    addField(words, false)
  }

  def repeated (words: StreamedWords) = {
    val fieldTypeStr = words.readWord.getOrElse(throw new ProtoFileParsingException("missing required field type on message %s".format(currentMessage.get.name), words.lineNumber))
    val fieldName = words.readWord.getOrElse(throw new ProtoFileParsingException("missing required field name on message %s".format(currentMessage.get.name), words.lineNumber))
    val assignSymbol = words.readWord.getOrElse(throw new ProtoFileParsingException("missing = on message %s".format(currentMessage.get.name), words.lineNumber))
    val fieldNumber = words.readWord.getOrElse(throw new ProtoFileParsingException("missing field number on message %s".format(currentMessage.get.name), words.lineNumber)).toInt

    if (!messageBodyStarted) throw new ProtoFileParsingException("can't declare a repeated field if the message hasn't been started", words.lineNumber)

    currentMessage.getOrElse(throw new ProtoFileParsingException("can't add field without a message definition", words.lineNumber)) match {
      case msg: ProtoMessage =>
        if (assignSymbol != "="){
          throw new ProtoFileParsingException("expected = for field name declaration", words.lineNumber)
        }

        val fieldType = ProtoFile.supportedTypes.get(fieldTypeStr) match {
          case Some(ftype) => ftype(getCodeGenContext)
          case None => MessageType(fieldTypeStr)
        }

        val field = ProtoTypeField(Repeated(fieldType, getCodeGenContext), fieldName, fieldNumber, false)
        msg.fields += (fieldName -> field)
        words
      case unknown =>
        throw new ProtoFileParsingException("can't add repeated field to non message type "+unknown.getClass.getName, words.lineNumber)
    }

  }

  def lineComment (words: StreamedWords) = {
    //skip all comments in the line
    words.readLine
    words
  }

  def validateMessages(importedMessages: Map[String, ProtoType]) {
    messages.foreach {
      case (msgName, msgDef) =>
        msgDef match {
          case mDef: ProtoMessage =>
            mDef.fields = mDef.fields.map {
              case (fieldName, fieldDef) =>
                val newType = fieldDef.fieldType match {
                  case fieldType: MessageType =>
                    validateMessageType(importedMessages, fieldType, fieldName)
                  case repeatedType: Repeated =>
                    repeatedType.repeatingType match {
                      case repeatingMsgType: MessageType =>
                        repeatedType.copy(repeatingType = validateMessageType(importedMessages, repeatingMsgType, fieldName))
                      case o => repeatedType
                    }

                  case otherType => otherType
                }
                (fieldName, fieldDef.copy(fieldType = newType))
            }
          case _ =>
        }

    }
  }

  private def validateMessageType(importedMessages: Map[String, ProtoType], fieldType: MessageType, fieldName: String) = {
    if (!importedMessages.contains(fieldType.name)){
      throw new IllegalArgumentException("can't find type %s for field %s".format(fieldType.name, fieldName))
    }

    importedMessages(fieldType.name) match {
      case m: ProtoEnumMessage => fieldType.copy(enumMsg = true)
      case m: ProtoMessage => fieldType
    }
  }

  private def addField(words: StreamedWords, required: Boolean) = {
    val fieldTypeStr = words.readWord.getOrElse(throw new ProtoFileParsingException("missing required field type on message %s".format(currentMessage.get.name), words.lineNumber))
    val fieldName = words.readWord.getOrElse(throw new ProtoFileParsingException("missing required field name on message %s".format(currentMessage.get.name), words.lineNumber))
    val assignSymbol = words.readWord.getOrElse(throw new ProtoFileParsingException("missing = on message %s".format(currentMessage.get.name), words.lineNumber))
    val fieldNumber = words.readWord.getOrElse(throw new ProtoFileParsingException("missing field number on message %s".format(currentMessage.get.name), words.lineNumber)).toInt

    if (!messageBodyStarted) throw new ProtoFileParsingException("can't declare a field if the message hasn't been started", words.lineNumber)
    currentMessage.getOrElse(throw new ProtoFileParsingException("can't add field without a message definition", words.lineNumber)) match {
      case msg: ProtoMessage =>
        if (assignSymbol != "="){
          throw new ProtoFileParsingException("expected = for field name declaration", words.lineNumber)
        }
        val fieldType = ProtoFile.supportedTypes.get(fieldTypeStr) match {
          case Some(ftype) => ftype(getCodeGenContext)
          case None => MessageType(fieldTypeStr)
        }

        val field = ProtoTypeField(fieldType, fieldName, fieldNumber, required)
        msg.fields += (fieldName -> field)
        words
      case unknown =>
        throw new ProtoFileParsingException("can't add field to non message type "+unknown.getClass.getName, words.lineNumber)
    }
  }
}

trait ProtoType {
  val name: String
  val packageDef: Option[String]
  var superClass: Option[String] = None
}

class ProtoMessage(val name: String, val packageDef: Option[String]) extends ProtoType {
  var fields = Map.empty[String, ProtoTypeField]
}

class ProtoEnumMessage(val name: String, val packageDef: Option[String]) extends ProtoType {
  var fields =Map.empty[String, ProtoTypeEnumField]
}

case class ProtoTypeField(@BeanProperty fieldType: FieldType, @BeanProperty fieldName: String, @BeanProperty fieldNumber: Int, required: Boolean){
  def getFieldGetter: String = "get"+getFirstCapName
  def getFieldSetter: String = "set"+getFirstCapName
  def getFirstCapName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)
}

case class ProtoTypeEnumField(@BeanProperty fieldName: String, @BeanProperty fieldNumber: Int){
  def getUpperFieldName: String = fieldName.toUpperCase
}

case class ProtoFileParsingException(msg: String, line: Int) extends RuntimeException(msg + " at line "+line)