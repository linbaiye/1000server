// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: java.proto

// Protobuf Java Version: 3.25.1
package org.y1000.network.gen;

/**
 * Protobuf type {@code org.y1000.network.gen.InterpolationPacket}
 */
public final class InterpolationPacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:org.y1000.network.gen.InterpolationPacket)
    InterpolationPacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use InterpolationPacket.newBuilder() to construct.
  private InterpolationPacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private InterpolationPacket() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new InterpolationPacket();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_InterpolationPacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_InterpolationPacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.y1000.network.gen.InterpolationPacket.class, org.y1000.network.gen.InterpolationPacket.Builder.class);
  }

  public static final int STATE_FIELD_NUMBER = 2;
  private int state_ = 0;
  /**
   * <code>int32 state = 2;</code>
   * @return The state.
   */
  @java.lang.Override
  public int getState() {
    return state_;
  }

  public static final int ELAPSEDMILLIS_FIELD_NUMBER = 5;
  private int elapsedMillis_ = 0;
  /**
   * <code>int32 elapsedMillis = 5;</code>
   * @return The elapsedMillis.
   */
  @java.lang.Override
  public int getElapsedMillis() {
    return elapsedMillis_;
  }

  public static final int DIRECTION_FIELD_NUMBER = 7;
  private int direction_ = 0;
  /**
   * <code>int32 direction = 7;</code>
   * @return The direction.
   */
  @java.lang.Override
  public int getDirection() {
    return direction_;
  }

  public static final int X_FIELD_NUMBER = 8;
  private int x_ = 0;
  /**
   * <code>int32 x = 8;</code>
   * @return The x.
   */
  @java.lang.Override
  public int getX() {
    return x_;
  }

  public static final int Y_FIELD_NUMBER = 9;
  private int y_ = 0;
  /**
   * <code>int32 y = 9;</code>
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
    if (state_ != 0) {
      output.writeInt32(2, state_);
    }
    if (elapsedMillis_ != 0) {
      output.writeInt32(5, elapsedMillis_);
    }
    if (direction_ != 0) {
      output.writeInt32(7, direction_);
    }
    if (x_ != 0) {
      output.writeInt32(8, x_);
    }
    if (y_ != 0) {
      output.writeInt32(9, y_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (state_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, state_);
    }
    if (elapsedMillis_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, elapsedMillis_);
    }
    if (direction_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(7, direction_);
    }
    if (x_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(8, x_);
    }
    if (y_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(9, y_);
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
    if (!(obj instanceof org.y1000.network.gen.InterpolationPacket)) {
      return super.equals(obj);
    }
    org.y1000.network.gen.InterpolationPacket other = (org.y1000.network.gen.InterpolationPacket) obj;

    if (getState()
        != other.getState()) return false;
    if (getElapsedMillis()
        != other.getElapsedMillis()) return false;
    if (getDirection()
        != other.getDirection()) return false;
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
    hash = (37 * hash) + STATE_FIELD_NUMBER;
    hash = (53 * hash) + getState();
    hash = (37 * hash) + ELAPSEDMILLIS_FIELD_NUMBER;
    hash = (53 * hash) + getElapsedMillis();
    hash = (37 * hash) + DIRECTION_FIELD_NUMBER;
    hash = (53 * hash) + getDirection();
    hash = (37 * hash) + X_FIELD_NUMBER;
    hash = (53 * hash) + getX();
    hash = (37 * hash) + Y_FIELD_NUMBER;
    hash = (53 * hash) + getY();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.y1000.network.gen.InterpolationPacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.network.gen.InterpolationPacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.network.gen.InterpolationPacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.network.gen.InterpolationPacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.network.gen.InterpolationPacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.network.gen.InterpolationPacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.network.gen.InterpolationPacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.network.gen.InterpolationPacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static org.y1000.network.gen.InterpolationPacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static org.y1000.network.gen.InterpolationPacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.y1000.network.gen.InterpolationPacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.network.gen.InterpolationPacket parseFrom(
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
  public static Builder newBuilder(org.y1000.network.gen.InterpolationPacket prototype) {
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
   * Protobuf type {@code org.y1000.network.gen.InterpolationPacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:org.y1000.network.gen.InterpolationPacket)
      org.y1000.network.gen.InterpolationPacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_InterpolationPacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_InterpolationPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.y1000.network.gen.InterpolationPacket.class, org.y1000.network.gen.InterpolationPacket.Builder.class);
    }

    // Construct using org.y1000.network.gen.InterpolationPacket.newBuilder()
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
      state_ = 0;
      elapsedMillis_ = 0;
      direction_ = 0;
      x_ = 0;
      y_ = 0;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_InterpolationPacket_descriptor;
    }

    @java.lang.Override
    public org.y1000.network.gen.InterpolationPacket getDefaultInstanceForType() {
      return org.y1000.network.gen.InterpolationPacket.getDefaultInstance();
    }

    @java.lang.Override
    public org.y1000.network.gen.InterpolationPacket build() {
      org.y1000.network.gen.InterpolationPacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.y1000.network.gen.InterpolationPacket buildPartial() {
      org.y1000.network.gen.InterpolationPacket result = new org.y1000.network.gen.InterpolationPacket(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(org.y1000.network.gen.InterpolationPacket result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.state_ = state_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.elapsedMillis_ = elapsedMillis_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.direction_ = direction_;
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
      if (other instanceof org.y1000.network.gen.InterpolationPacket) {
        return mergeFrom((org.y1000.network.gen.InterpolationPacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.y1000.network.gen.InterpolationPacket other) {
      if (other == org.y1000.network.gen.InterpolationPacket.getDefaultInstance()) return this;
      if (other.getState() != 0) {
        setState(other.getState());
      }
      if (other.getElapsedMillis() != 0) {
        setElapsedMillis(other.getElapsedMillis());
      }
      if (other.getDirection() != 0) {
        setDirection(other.getDirection());
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
            case 16: {
              state_ = input.readInt32();
              bitField0_ |= 0x00000001;
              break;
            } // case 16
            case 40: {
              elapsedMillis_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 40
            case 56: {
              direction_ = input.readInt32();
              bitField0_ |= 0x00000004;
              break;
            } // case 56
            case 64: {
              x_ = input.readInt32();
              bitField0_ |= 0x00000008;
              break;
            } // case 64
            case 72: {
              y_ = input.readInt32();
              bitField0_ |= 0x00000010;
              break;
            } // case 72
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

    private int state_ ;
    /**
     * <code>int32 state = 2;</code>
     * @return The state.
     */
    @java.lang.Override
    public int getState() {
      return state_;
    }
    /**
     * <code>int32 state = 2;</code>
     * @param value The state to set.
     * @return This builder for chaining.
     */
    public Builder setState(int value) {

      state_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int32 state = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearState() {
      bitField0_ = (bitField0_ & ~0x00000001);
      state_ = 0;
      onChanged();
      return this;
    }

    private int elapsedMillis_ ;
    /**
     * <code>int32 elapsedMillis = 5;</code>
     * @return The elapsedMillis.
     */
    @java.lang.Override
    public int getElapsedMillis() {
      return elapsedMillis_;
    }
    /**
     * <code>int32 elapsedMillis = 5;</code>
     * @param value The elapsedMillis to set.
     * @return This builder for chaining.
     */
    public Builder setElapsedMillis(int value) {

      elapsedMillis_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>int32 elapsedMillis = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearElapsedMillis() {
      bitField0_ = (bitField0_ & ~0x00000002);
      elapsedMillis_ = 0;
      onChanged();
      return this;
    }

    private int direction_ ;
    /**
     * <code>int32 direction = 7;</code>
     * @return The direction.
     */
    @java.lang.Override
    public int getDirection() {
      return direction_;
    }
    /**
     * <code>int32 direction = 7;</code>
     * @param value The direction to set.
     * @return This builder for chaining.
     */
    public Builder setDirection(int value) {

      direction_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>int32 direction = 7;</code>
     * @return This builder for chaining.
     */
    public Builder clearDirection() {
      bitField0_ = (bitField0_ & ~0x00000004);
      direction_ = 0;
      onChanged();
      return this;
    }

    private int x_ ;
    /**
     * <code>int32 x = 8;</code>
     * @return The x.
     */
    @java.lang.Override
    public int getX() {
      return x_;
    }
    /**
     * <code>int32 x = 8;</code>
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
     * <code>int32 x = 8;</code>
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
     * <code>int32 y = 9;</code>
     * @return The y.
     */
    @java.lang.Override
    public int getY() {
      return y_;
    }
    /**
     * <code>int32 y = 9;</code>
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
     * <code>int32 y = 9;</code>
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


    // @@protoc_insertion_point(builder_scope:org.y1000.network.gen.InterpolationPacket)
  }

  // @@protoc_insertion_point(class_scope:org.y1000.network.gen.InterpolationPacket)
  private static final org.y1000.network.gen.InterpolationPacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.y1000.network.gen.InterpolationPacket();
  }

  public static org.y1000.network.gen.InterpolationPacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<InterpolationPacket>
      PARSER = new com.google.protobuf.AbstractParser<InterpolationPacket>() {
    @java.lang.Override
    public InterpolationPacket parsePartialFrom(
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

  public static com.google.protobuf.Parser<InterpolationPacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<InterpolationPacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.y1000.network.gen.InterpolationPacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

