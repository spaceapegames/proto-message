using System;
using System.Collections.Generic;
using System.Reflection;
using Google.ProtocolBuffers.Collections;

//namespace Google.ProtocolBuffers
//{
//  [CLSCompliant(false)]
//  [AttributeUsage(AttributeTargets.Field)]
//  public sealed class FieldMappingAttribute : Attribute
//  {
//    public FieldMappingAttribute(MappedType mappedType, WireFormat.WireType wireType)
//    {
//      MappedType = mappedType;
//      WireType = wireType;
//    }
//
//    public MappedType MappedType { get; private set; }
//
//    public WireFormat.WireType WireType { get; private set; }
//
//
//    /// <summary>
//    /// Immutable mapping from field type to mapped type. Built using the attributes on
//    /// FieldType values.
//    /// </summary>
//    private static readonly IDictionary<FieldType, FieldMappingAttribute> FieldTypeToMappedTypeMap = MapFieldTypes();
//
//    private static IDictionary<FieldType, FieldMappingAttribute> MapFieldTypes()
//    {
//      var map = new Dictionary<FieldType, FieldMappingAttribute>();
//      foreach (FieldInfo field in typeof(FieldType).GetFields(BindingFlags.Static | BindingFlags.Public))
//      {
//        FieldType fieldType = (FieldType)field.GetValue(null);
//        FieldMappingAttribute mapping =
//          (FieldMappingAttribute)field.GetCustomAttributes(typeof(FieldMappingAttribute), false)[0];
//        map[fieldType] = mapping;
//      }
//      return Dictionaries.AsReadOnly(map);
//    }
//
//    internal static MappedType MappedTypeFromFieldType(FieldType type)
//    {
//      return FieldTypeToMappedTypeMap[type].MappedType;
//    }
//
//    internal static WireFormat.WireType WireTypeFromFieldType(FieldType type, bool packed)
//    {
//      return packed ? WireFormat.WireType.LengthDelimited : FieldTypeToMappedTypeMap[type].WireType;
//    }
//  }
//}

