// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message.proto

// Protobuf Java Version: 3.25.0-rc2
package org.y1000.connection.gen;

/**
 * Protobuf enum {@code org.y1000.connection.gen.CreatureType}
 */
public enum CreatureType
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>DEER = 0;</code>
   */
  DEER(0),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>DEER = 0;</code>
   */
  public static final int DEER_VALUE = 0;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static CreatureType valueOf(int value) {
    return forNumber(value);
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   */
  public static CreatureType forNumber(int value) {
    switch (value) {
      case 0: return DEER;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<CreatureType>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      CreatureType> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<CreatureType>() {
          public CreatureType findValueByNumber(int number) {
            return CreatureType.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalStateException(
          "Can't get the descriptor of an unrecognized enum value.");
    }
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return org.y1000.connection.gen.Message.getDescriptor().getEnumTypes().get(2);
  }

  private static final CreatureType[] VALUES = values();

  public static CreatureType valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private CreatureType(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:org.y1000.connection.gen.CreatureType)
}

