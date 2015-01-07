package com.spaceape.protobuf.lang

import org.scalatest.ShouldMatchers
import org.testng.annotations.Test

class StreamedArrayWordsTest extends ShouldMatchers{
  @Test
  def testRead {
    val content = """
    import common.proto
    import model2.proto

    package com.spaceape.model

    enum ResourceTypeTO {
      Solid = 1
      Liquid = 2
    }

    message Entity {
      required string id = 1
    }
                  """
    val streamedWords = new StreamedArrayWords(content)
    streamedWords.readWord should equal (Some("import"))
    streamedWords.readWord should equal (Some("common.proto"))
    streamedWords.readWord should equal (Some("import"))
    streamedWords.readLine.toList should equal (List("model2.proto"))
    streamedWords.readWord should equal (Some("package"))
    streamedWords.readLine
    streamedWords.readLine
    streamedWords.readLine
    streamedWords.readLine
    streamedWords.readLine
    streamedWords.readLine
    streamedWords.readLine
    streamedWords.readWord should equal (Some("}"))
    streamedWords.hasNext should be (false)
    streamedWords.readLine.toList should equal(List.empty[String])
    streamedWords.readWord should equal (None)
  }
}
