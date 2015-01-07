package com.spaceape.protobuf.lang

trait StreamedWords {
  def readWord: Option[String]
  def readLine: Array[String]
  def lineNumber: Int
  def hasNext: Boolean
}

class StreamedArrayWords(content: String) extends StreamedWords{
  val lines: Array[Array[String]] = init
  var index: Int = 0
  var lineIndex = 0

  def init: Array[Array[String]] = {
    return content.replaceAll("\\{", " { ")
      .replaceAll("\\}", " } ")
      .replaceAll("=", " = ")
      .replaceAll("//", " // ")
      .split("\n").filterNot(line => {line == null || line.trim.isEmpty}).map(_.split(" ").filter(token => {token!=null && !token.trim.isEmpty}))
  }
  def readWord: Option[String] = {
    if (lineIndex >= lines.length) return None
    if (index >= lines(lineIndex).length) return None

    val word = Some(lines(lineIndex)(index))
    index += 1
    if (index >= lines(lineIndex).length){
      index = 0
      lineIndex += 1
    }
    word
  }

  def readLine: Array[String] = {
    if (lineIndex >= lines.length) return Array.empty[String]
    val line = lines(lineIndex).slice(index, lines(lineIndex).length)
    lineIndex += 1
    index = 0
    line
  }

  def hasNext: Boolean = {
    if (lineIndex < lines.length - 1){
      return true
    }
    if (lineIndex == lines.length - 1 && index < lines(lineIndex).length){
      return true
    }
    return false
  }

  def lineNumber: Int = lineIndex
}