package com.spaceape.protobuf.lang

import java.io.File

class ProtoContext {
  var loadingPaths = Set.empty[String]
  var loadedFiles = Map.empty[String, ProtoFile]

  def loadProtoFile(filename: String) {
    if (loadedFiles.contains(filename)) return

    val file = loadingPaths.find(path => {
      val filesrc = new File(path + filename)
      println(filesrc.getAbsolutePath)
      filesrc.exists()
    }
    ).map(_ + filename).getOrElse(throw new IllegalArgumentException("can't find file %s in paths %s".format(filename, loadingPaths)))
    val source = scala.io.Source.fromFile(file)
    val content = source.getLines() mkString (" ")
    source.close()

    val protoFile = new ProtoFile(Some(filename))
    loadedFiles += (filename -> protoFile)
    ProtoFile.parse(content, this, protoFile)

  }
}

object ScopedMessages {
  def apply(root: ProtoFile, context: ProtoContext) = {
    val messages = scala.collection.mutable.Map.empty[String, ProtoType]
    messages ++= root.messages
    val importedFiles = scala.collection.mutable.Set[Option[String]](root.filename)

    root.importedFiles.foreach {
      filename =>
        importMessage(filename, context, messages, importedFiles)
    }
    messages.toMap
  }

  private def importMessage(filename: String, context: ProtoContext, importedMessages: scala.collection.mutable.Map[String, ProtoType], importedFiles: scala.collection.mutable.Set[Option[String]]) {
    if (importedFiles.contains(Some(filename))) return

    val protofile = context.loadedFiles.getOrElse(filename, throw new IllegalArgumentException("unimported file %s".format(filename)))
    importedFiles += Some(filename)
    protofile.messages.foreach {
      case (name, message) =>
        if (importedMessages.contains(name)) throw new IllegalArgumentException("message name conflict. message %s has been defined at two places".format(name))
        importedMessages += (name -> message)
    }

    protofile.importedFiles.foreach {
      importedFilename =>
        importMessage(importedFilename, context, importedMessages, importedFiles)
    }
  }
}
