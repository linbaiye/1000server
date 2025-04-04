// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: java.proto

// Protobuf Java Version: 3.25.1
package org.y1000.network.gen;

/**
 * Protobuf type {@code org.y1000.network.gen.MonsterMoveEventPacket}
 */
public final class MonsterMoveEventPacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:org.y1000.network.gen.MonsterMoveEventPacket)
    MonsterMoveEventPacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use MonsterMoveEventPacket.newBuilder() to construct.
  private MonsterMoveEventPacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private MonsterMoveEventPacket() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new MonsterMoveEventPacket();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_MonsterMoveEventPacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_MonsterMoveEventPacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.y1000.network.gen.MonsterMoveEventPacket.class, org.y1000.network.gen.MonsterMoveEventPacket.Builder.class);
  }

  public static final int ID_FIELD_NUMBER = 1;
  private long id_ = 0L;
  /**
   * <code>int64 id = 1;</code>
   * @return The id.
   */
  @java.lang.Override
  public long getId() {
    return id_;
  }

  public static final int DIRECTION_FIELD_NUMBER = 2;
  private int direction_ = 0;
  /**
   * <code>int32 direction = 2;</code>
   * @return The direction.
   */
  @java.lang.Override
  public int getDirection() {
    return direction_;
  }

  public static final int SPEED_FIELD_NUMBER = 3;
  private int speed_ = 0;
  /**
   * <code>int32 speed = 3;</code>
   * @return The speed.
   */
  @java.lang.Override
  public int getSpeed() {
    return speed_;
  }

  public static final int X_FIELD_NUMBER = 4;
  private int x_ = 0;
  /**
   * <code>int32 x = 4;</code>
   * @return The x.
   */
  @java.lang.Override
  public int getX() {
    return x_;
  }

  public static final int Y_FIELD_NUMBER = 5;
  private int y_ = 0;
  /**
   * <code>int32 y = 5;</code>
   * @return The y.
   */
  @java.lang.Override
  public int getY() {
    return y_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (id_ != 0L) {
      output.writeInt64(1, id_);
    }
    if (direction_ != 0) {
      output.writeInt32(2, direction_);
    }
    if (speed_ != 0) {
      output.writeInt32(3, speed_);
    }
    if (x_ != 0) {
      output.writeInt32(4, x_);
    }
    if (y_ != 0) {
      output.writeInt32(5, y_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (id_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, id_);
    }
    if (direction_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, direction_);
    }
    if (speed_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, speed_);
    }
    if (x_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, x_);
    }
    if (y_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, y_);
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.y1000.network.gen.MonsterMoveEventPacket)) {
      return super.equals(obj);
    }
    org.y1000.network.gen.MonsterMoveEventPacket other = (org.y1000.network.gen.MonsterMoveEventPacket) obj;

    if (getId()
        != other.getId()) return false;
    if (getDirection()
        != other.getDirection()) return false;
    if (getSpeed()
        != other.getSpeed()) return false;
    if (getX()
        != other.getX()) return false;
    if (getY()
        != other.getY()) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + ID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getId());
    hash = (37 * hash) + DIRECTION_FIELD_NUMBER;
    hash = (53 * hash) + getDirection();
    hash = (37 * hash) + SPEED_FIELD_NUMBER;
    hash = (53 * hash) + getSpeed();
    hash = (37 * hash) + X_FIELD_NUMBER;
    hash = (53 * hash) + getX();
    hash = (37 * hash) + Y_FIELD_NUMBER;
    hash = (53 * hash) + getY();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static org.y1000.network.gen.MonsterMoveEventPacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static org.y1000.network.gen.MonsterMoveEventPacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.network.gen.MonsterMoveEventPacket parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.y1000.network.gen.MonsterMoveEventPacket prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code org.y1000.network.gen.MonsterMoveEventPacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:org.y1000.network.gen.MonsterMoveEventPacket)
      org.y1000.network.gen.MonsterMoveEventPacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_MonsterMoveEventPacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_MonsterMoveEventPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.y1000.network.gen.MonsterMoveEventPacket.class, org.y1000.network.gen.MonsterMoveEventPacket.Builder.class);
    }

    // Construct using org.y1000.network.gen.MonsterMoveEventPacket.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      id_ = 0L;
      direction_ = 0;
      speed_ = 0;
      x_ = 0;
      y_ = 0;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_MonsterMoveEventPacket_descriptor;
    }

    @java.lang.Override
    public org.y1000.network.gen.MonsterMoveEventPacket getDefaultInstanceForType() {
      return org.y1000.network.gen.MonsterMoveEventPacket.getDefaultInstance();
    }

    @java.lang.Override
    public org.y1000.network.gen.MonsterMoveEventPacket build() {
      org.y1000.network.gen.MonsterMoveEventPacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.y1000.network.gen.MonsterMoveEventPacket buildPartial() {
      org.y1000.network.gen.MonsterMoveEventPacket result = new org.y1000.network.gen.MonsterMoveEventPacket(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(org.y1000.network.gen.MonsterMoveEventPacket result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.id_ = id_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.direction_ = direction_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.speed_ = speed_;
      }
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.x_ = x_;
      }
      if (((from_bitField0_ & 0x00000010) != 0)) {
        result.y_ = y_;
      }
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.y1000.network.gen.MonsterMoveEventPacket) {
        return mergeFrom((org.y1000.network.gen.MonsterMoveEventPacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.y1000.network.gen.MonsterMoveEventPacket other) {
      if (other == org.y1000.network.gen.MonsterMoveEventPacket.getDefaultInstance()) return this;
      if (other.getId() != 0L) {
        setId(other.getId());
      }
      if (other.getDirection() != 0) {
        setDirection(other.getDirection());
      }
      if (other.getSpeed() != 0) {
        setSpeed(other.getSpeed());
      }
      if (other.getX() != 0) {
        setX(other.getX());
      }
      if (other.getY() != 0) {
        setY(other.getY());
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              id_ = input.readInt64();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 16: {
              direction_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
            case 24: {
              speed_ = input.readInt32();
              bitField0_ |= 0x00000004;
              break;
            } // case 24
            case 32: {
              x_ = input.readInt32();
              bitField0_ |= 0x00000008;
              break;
            } // case 32
            case 40: {
              y_ = input.readInt32();
              bitField0_ |= 0x00000010;
              break;
            } // case 40
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private long id_ ;
    /**
     * <code>int64 id = 1;</code>
     * @return The id.
     */
    @java.lang.Override
    public long getId() {
      return id_;
    }
    /**
     * <code>int64 id = 1;</code>
     * @param value The id to set.
     * @return This builder for chaining.
     */
    public Builder setId(long value) {

      id_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int64 id = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearId() {
      bitField0_ = (bitField0_ & ~0x00000001);
      id_ = 0L;
      onChanged();
      return this;
    }

    private int direction_ ;
    /**
     * <code>int32 direction = 2;</code>
     * @return The direction.
     */
    @java.lang.Override
    public int getDirection() {
      return direction_;
    }
    /**
     * <code>int32 direction = 2;</code>
     * @param value The direction to set.
     * @return This builder for chaining.
     */
    public Builder setDirection(int value) {

      direction_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>int32 direction = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearDirection() {
      bitField0_ = (bitField0_ & ~0x00000002);
      direction_ = 0;
      onChanged();
      return this;
    }

    private int speed_ ;
    /**
     * <code>int32 speed = 3;</code>
     * @return The speed.
     */
    @java.lang.Override
    public int getSpeed() {
      return speed_;
    }
    /**
     * <code>int32 speed = 3;</code>
     * @param value The speed to set.
     * @return This builder for chaining.
     */
    public Builder setSpeed(int value) {

      speed_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>int32 speed = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearSpeed() {
      bitField0_ = (bitField0_ & ~0x00000004);
      speed_ = 0;
      onChanged();
      return this;
    }

    private int x_ ;
    /**
     * <code>int32 x = 4;</code>
     * @return The x.
     */
    @java.lang.Override
    public int getX() {
      return x_;
    }
    /**
     * <code>int32 x = 4;</code>
     * @param value The x to set.
     * @return This builder for chaining.
     */
    public Builder setX(int value) {

      x_ = value;
      bitField0_ |= 0x00000008;
      onChanged();
      return this;
    }
    /**
     * <code>int32 x = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearX() {
      bitField0_ = (bitField0_ & ~0x00000008);
      x_ = 0;
      onChanged();
      return this;
    }

    private int y_ ;
    /**
     * <code>int32 y = 5;</code>
     * @return The y.
     */
    @java.lang.Override
    public int getY() {
      return y_;
    }
    /**
     * <code>int32 y = 5;</code>
     * @param value The y to set.
     * @return This builder for chaining.
     */
    public Builder setY(int value) {

      y_ = value;
      bitField0_ |= 0x00000010;
      onChanged();
      return this;
    }
    /**
     * <code>int32 y = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearY() {
      bitField0_ = (bitField0_ & ~0x00000010);
      y_ = 0;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:org.y1000.network.gen.MonsterMoveEventPacket)
  }

  // @@protoc_insertion_point(class_scope:org.y1000.network.gen.MonsterMoveEventPacket)
  private static final org.y1000.network.gen.MonsterMoveEventPacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.y1000.network.gen.MonsterMoveEventPacket();
  }

  public static org.y1000.network.gen.MonsterMoveEventPacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<MonsterMoveEventPacket>
      PARSER = new com.google.protobuf.AbstractParser<MonsterMoveEventPacket>() {
    @java.lang.Override
    public MonsterMoveEventPacket parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<MonsterMoveEventPacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<MonsterMoveEventPacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.y1000.network.gen.MonsterMoveEventPacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

