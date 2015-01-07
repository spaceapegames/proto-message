package com.spaceape.protobuf.codegen

import com.spaceape.protobuf.lang.{ProtoEnumMessage, ProtoMessage, ProtoContext, ProtoFile}
import org.stringtemplate.v4.{STRawGroupDir, STGroup, ST}
import java.io.{File, FileOutputStream, BufferedOutputStream}
import scala.beans.BeanProperty

object CodeGen {
  val properties = Map[String, Any](
    "patch" -> false,
    "reflect" -> false
  )
}
class CodeGen(templateDir: String, lang: String = "java", properties: Map[String, Any] = CodeGen.properties) {
  val DEFAULT_PACKAGE_NAME = "com.spaceape.gen"
  val mapper = TypeMappers.mappers(lang)
  val stGroup: STGroup = new STRawGroupDir(templateDir, '$', '$');

  def generate(fileName: String, context: ProtoContext, toFolder: String) {
    val allFiles = scala.collection.mutable.Map.empty[String, ProtoFile]
    importFile(fileName, context, allFiles)

    generateRegistry(toFolder, allFiles.values.foldLeft(Set.empty[MessageType]){
      case (s, n) =>
        s ++ n.messages.values.filter(_.isInstanceOf[ProtoMessage]).map{
          msg =>
            MessageType(msg.name, msg.packageDef.getOrElse("")+"."+msg.name)
        }
    })

    allFiles.values.foreach {
      protoFile =>
        protoFile.codeGenContext = CodeGenContext(lang, mapper)
        protoFile.messages.foreach {
          case (name, message: ProtoMessage) =>
            val templateName = mapper.templateMap(message.getClass.getSimpleName)
            val template: ST = stGroup.getInstanceOf(templateName)

            val packageName = protoFile.packageName.getOrElse(DEFAULT_PACKAGE_NAME)
            template.add("packageName", packageName)
            template.add("className", name)
            template.add("superClassName", message.superClass.getOrElse("GeneratedMessage"))
            template.add("fields", message.fields.values.toArray)
            template.add("importedPacks", protoFile.importedPacks.values.filter(_.isDefined).map(_.get).toArray)
            properties.foreach {
              case (name, value) =>
                template.add(name, value)
            }
            val code = template.render()
            outputCode(code, name, toFolder, packageName)
          case (name, message: ProtoEnumMessage) =>
            val templateName = mapper.templateMap(message.getClass.getSimpleName)
            val template: ST = stGroup.getInstanceOf(templateName)

            val packageName = protoFile.packageName.getOrElse(DEFAULT_PACKAGE_NAME)
            template.add("packageName", packageName)
            template.add("className", name)
            template.add("fields", message.fields.values.toArray)
            properties.foreach {
              case (name, value) =>
                template.add(name, value)
            }
            val code = template.render()
            outputCode(code, name, toFolder, packageName)
        }
    }

  }

  private def outputCode(code: String, name: String, toFolder: String, packageName: String){
    val packageFolder = new File(toFolder + "/" + packageName.replaceAll("\\.", "/"))
    if (!packageFolder.exists()) {
      packageFolder.mkdirs()
    }

    val output = new BufferedOutputStream(new FileOutputStream(new File(packageFolder, "/" + name + mapper.filePostfix)))
    try {
      output.write(code.getBytes)
      output.flush()
    } finally {
      output.close()
    }
  }

  private def generateRegistry (toFolder: String, msgs: Set[MessageType]){
    val name = "ClassRegistry"
    val packageName = "com.spaceape.protobuf"
    val template: ST = stGroup.getInstanceOf(mapper.templateMap(name))
    template.add("packageName", packageName)
    template.add("messageTypes", msgs.toArray)

    val code = template.render()
    outputCode(code, name, toFolder, packageName)
  }

  private def importFile(fileName: String, context: ProtoContext, importedFiles: scala.collection.mutable.Map[String, ProtoFile]) {
    if (importedFiles.contains(fileName)) {
      return
    }
    val protoFile: ProtoFile = context.loadedFiles(fileName)
    importedFiles +=(fileName -> protoFile)
    protoFile.importedFiles.foreach {
      importedFile =>
        importFile(importedFile, context, importedFiles)
    }
  }
}
case class MessageType (@BeanProperty name: String, @BeanProperty fullName: String)