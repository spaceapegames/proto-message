using System;

namespace Google.ProtocolBuffers
{
  public static class WireFormat
  {
    #region Fixed sizes.

    // TODO(jonskeet): Move these somewhere else. They're messy. Consider making FieldType a smarter kind of enum
    public const int Fixed32Size = 4;
    public const int Fixed64Size = 8;
    public const int SFixed32Size = 4;
    public const int SFixed64Size = 8;
    public const int FloatSize = 4;
    public const int DoubleSize = 8;
    public const int BoolSize = 1;

    #endregion

    [CLSCompliant(false)]
    public enum WireType : uint
    {
      Varint = 0,
      Fixed64 = 1,
      LengthDelimited = 2,
      StartGroup = 3,
      EndGroup = 4,
      Fixed32 = 5
    }

    internal static class MessageSetField
    {
      internal const int Item = 1;
      internal const int TypeID = 2;
      internal const int Message = 3;
    }

    internal static class MessageSetTag
    {
      internal static readonly uint ItemStart = MakeTag(MessageSetField.Item, WireType.StartGroup);
      internal static readonly uint ItemEnd = MakeTag(MessageSetField.Item, WireType.EndGroup);
      internal static readonly uint TypeID = MakeTag(MessageSetField.TypeID, WireType.Varint);
      internal static readonly uint Message = MakeTag(MessageSetField.Message, WireType.LengthDelimited);
    }

    private const int TagTypeBits = 3;
    private const uint TagTypeMask = (1 << TagTypeBits) - 1;

    /// <summary>
    /// Given a tag value, determines the wire type (lower 3 bits).
    /// </summary>
    [CLSCompliant(false)]
    public static WireType GetTagWireType(uint tag)
    {
      return (WireType)(tag & TagTypeMask);
    }

    [CLSCompliant(false)]
    public static bool IsEndGroupTag(uint tag)
    {
      return (WireType)(tag & TagTypeMask) == WireType.EndGroup;
    }

    /// <summary>
    /// Given a tag value, determines the field number (the upper 29 bits).
    /// </summary>
    [CLSCompliant(false)]
    public static int GetTagFieldNumber(uint tag)
    {
      return (int)tag >> TagTypeBits;
    }

    /// <summary>
    /// Makes a tag value given a field number and wire type.
    /// TODO(jonskeet): Should we just have a Tag structure?
    /// </summary>
    [CLSCompliant(false)]
    public static uint MakeTag(int fieldNumber, WireType wireType)
    {
      return (uint)(fieldNumber << TagTypeBits) | (uint)wireType;
    }

    /// <summary>
    /// Converts a field type to its wire type. Done with a switch for the sake
    /// of speed - this is significantly faster than a dictionary lookup.
    /// </summary>
//    [CLSCompliant(false)]
//    public static WireType GetWireType(FieldType fieldType)
//    {
//      switch (fieldType)
//      {
//        case FieldType.Double:
//          return WireType.Fixed64;
//        case FieldType.Float:
//          return WireType.Fixed32;
//        case FieldType.Int64:
//        case FieldType.UInt64:
//        case FieldType.Int32:
//          return WireType.Varint;
//        case FieldType.Fixed64:
//          return WireType.Fixed64;
//        case FieldType.Fixed32:
//          return WireType.Fixed32;
//        case FieldType.Bool:
//          return WireType.Varint;
//        case FieldType.String:
//          return WireType.LengthDelimited;
//        case FieldType.Group:
//          return WireType.StartGroup;
//        case FieldType.Message:
//        case FieldType.Bytes:
//          return WireType.LengthDelimited;
//        case FieldType.UInt32:
//          return WireType.Varint;
//        case FieldType.SFixed32:
//          return WireType.Fixed32;
//        case FieldType.SFixed64:
//          return WireType.Fixed64;
//        case FieldType.SInt32:
//        case FieldType.SInt64:
//        case FieldType.Enum:
//          return WireType.Varint;
//        default:
//          throw new ArgumentOutOfRangeException("No such field type");
//      }
//    }
  }
}

