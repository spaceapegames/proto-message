package com.spaceape.protobuf.lang

import org.scalatest.matchers.ShouldMatchers
import org.testng.annotations.Test

class ProtoFileTest extends ShouldMatchers{
  @Test
  def testParse {
    val content =
    """
      package com.spaceape.generated

      message Entity {
        required string id = 1 //pp iio ooo
      }

      enum ResourceTypeTO {
          Solid = 1
          Liquid = 2
      }

      message ProfileTO extends Entity{
        required string name=10
        required int32 counter = 20
        optional Entity clan = 30
        repeated ProfileTO friends = 40
      }"""
    val protoFile = ProtoFile.parse(content, new ProtoContext, new ProtoFile())
    protoFile.packageName should equal (Some("com.spaceape.generated"))
    protoFile.messages("ProfileTO") match {
      case profileTO: ProtoMessage =>
        profileTO.superClass should equal (Some("Entity"))
        profileTO.fields("name").fieldNumber should equal (10)
        profileTO.fields("name").fieldType.isInstanceOf[string] should equal (true)
        profileTO.fields("clan").fieldNumber should equal (30)
        profileTO.fields("clan").fieldType should equal (MessageType("Entity"))

        val ftype = profileTO.fields("friends").fieldType
        ftype.isInstanceOf[Repeated] should be (true)
        ftype.asInstanceOf[Repeated].repeatingType should equal (MessageType("ProfileTO"))
      case unknown =>
        fail("unexpected message type "+unknown.getClass.getName)
    }

    protoFile.messages("ResourceTypeTO") match {
      case enum: ProtoEnumMessage =>
        enum.fields should equal (Map("Solid" -> ProtoTypeEnumField("Solid", 1), "Liquid" -> ProtoTypeEnumField("Liquid", 2)))
      case unknown =>
        fail("unexpected message type "+unknown.getClass.getName)
    }
  }

  @Test
  def testParseUnknownTypeFailure {
    val content =
      """
      message Entity {
        required string id = 1 //pp iio ooo
      }

      message ProfileTO extends Entity{
        required string name = 10
        required int32 counter = 20
        optional EntityTO clanId = 30
      }"""
    an [IllegalArgumentException] should be thrownBy {
      ProtoFile.parse(content, new ProtoContext, new ProtoFile())
    }

  }

  @Test
  def testDoubleDefinedMessage {
    val content =
      """
      message Entity {
        required string id = 1
      }

      message ProfileTO extends Entity{
        required string name = 10
        required int32 counter = 20
        optional Entity clanId = 30
      }
      message ProfileTO extends Entity{
        required string name = 10
        required int32 counter = 20
        optional Entity clanId = 30
      }"""
    an [ProtoFileParsingException] should be thrownBy {
      ProtoFile.parse(content, new ProtoContext, new ProtoFile())
    }

  }
}
