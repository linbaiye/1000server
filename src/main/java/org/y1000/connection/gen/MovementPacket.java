// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message.proto

// Protobuf Java Version: 3.25.1
package org.y1000.connection.gen;

/**
 * Protobuf type {@code org.y1000.connection.gen.MovementPacket}
 */
public final class MovementPacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:org.y1000.connection.gen.MovementPacket)
    MovementPacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use MovementPacket.newBuilder() to construct.
  private MovementPacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private MovementPacket() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new MovementPacket();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_MovementPacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_MovementPacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.y1000.connection.gen.MovementPacket.class, org.y1000.connection.gen.MovementPacket.Builder.class);
  }

  public static final int STATE_FIELD_NUMBER = 1;
  private int state_ = 0;
  /**
   * <code>int32 state = 1;</code>
   * @return The state.
   */
  @java.lang.Override
  public int getState() {
    return state_;
  }

  public static final int X_FIELD_NUMBER = 2;
  private int x_ = 0;
  /**
   * <code>int32 x = 2;</code>
   * @return The x.
   */
  @java.lang.Override
  public int getX() {
    return x_;
  }

  public static final int Y_FIELD_NUMBER = 3;
  private int y_ = 0;
  /**
   * <code>int32 y = 3;</code>
   * @return The y.
   */
  @java.lang.Override
  public int getY() {
    return y_;
  }

  public static final int DIRECTION_FIELD_NUMBER = 4;
  private int direction_ = 0;
  /**
   * <code>int32 direction = 4;</code>
   * @return The direction.
   */
  @java.lang.Override
  public int getDirection() {
    return direction_;
  }

  public static final int ID_FIELD_NUMBER = 5;
  private int id_ = 0;
  /**
   * <code>int32 id = 5;</code>
   * @return The id.
   */
  @java.lang.Override
  public int getId() {
    return id_;
  }

  public static final int TIMESTAMP_FIELD_NUMBER = 6;
  private long timestamp_ = 0L;
  /**
   * <code>int64 timestamp = 6;</code>
   * @return The timestamp.
   */
  @java.lang.Override
  public long getTimestamp() {
    return timestamp_;
  }

  public static final int SEQUENCE_FIELD_NUMBER = 7;
  private long sequence_ = 0L;
  /**
   * <code>int64 sequence = 7;</code>
   * @return The sequence.
   */
  @java.lang.Override
  public long getSequence() {
    return sequence_;
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
      output.writeInt32(1, state_);
    }
    if (x_ != 0) {
      output.writeInt32(2, x_);
    }
    if (y_ != 0) {
      output.writeInt32(3, y_);
    }
    if (direction_ != 0) {
      output.writeInt32(4, direction_);
    }
    if (id_ != 0) {
      output.writeInt32(5, id_);
    }
    if (timestamp_ != 0L) {
      output.writeInt64(6, timestamp_);
    }
    if (sequence_ != 0L) {
      output.writeInt64(7, sequence_);
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
        .computeInt32Size(1, state_);
    }
    if (x_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, x_);
    }
    if (y_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, y_);
    }
    if (direction_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, direction_);
    }
    if (id_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, id_);
    }
    if (timestamp_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(6, timestamp_);
    }
    if (sequence_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(7, sequence_);
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
    if (!(obj instanceof org.y1000.connection.gen.MovementPacket)) {
      return super.equals(obj);
    }
    org.y1000.connection.gen.MovementPacket other = (org.y1000.connection.gen.MovementPacket) obj;

    if (getState()
        != other.getState()) return false;
    if (getX()
        != other.getX()) return false;
    if (getY()
        != other.getY()) return false;
    if (getDirection()
        != other.getDirection()) return false;
    if (getId()
        != other.getId()) return false;
    if (getTimestamp()
        != other.getTimestamp()) return false;
    if (getSequence()
        != other.getSequence()) return false;
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
    hash = (37 * hash) + X_FIELD_NUMBER;
    hash = (53 * hash) + getX();
    hash = (37 * hash) + Y_FIELD_NUMBER;
    hash = (53 * hash) + getY();
    hash = (37 * hash) + DIRECTION_FIELD_NUMBER;
    hash = (53 * hash) + getDirection();
    hash = (37 * hash) + ID_FIELD_NUMBER;
    hash = (53 * hash) + getId();
    hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getTimestamp());
    hash = (37 * hash) + SEQUENCE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getSequence());
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.y1000.connection.gen.MovementPacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.connection.gen.MovementPacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.connection.gen.MovementPacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.connection.gen.MovementPacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.connection.gen.MovementPacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.connection.gen.MovementPacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.connection.gen.MovementPacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.connection.gen.MovementPacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static org.y1000.connection.gen.MovementPacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static org.y1000.connection.gen.MovementPacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.y1000.connection.gen.MovementPacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.connection.gen.MovementPacket parseFrom(
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
  public static Builder newBuilder(org.y1000.connection.gen.MovementPacket prototype) {
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
   * Protobuf type {@code org.y1000.connection.gen.MovementPacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:org.y1000.connection.gen.MovementPacket)
      org.y1000.connection.gen.MovementPacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_MovementPacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_MovementPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.y1000.connection.gen.MovementPacket.class, org.y1000.connection.gen.MovementPacket.Builder.class);
    }

    // Construct using org.y1000.connection.gen.MovementPacket.newBuilder()
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
      x_ = 0;
      y_ = 0;
      direction_ = 0;
      id_ = 0;
      timestamp_ = 0L;
      sequence_ = 0L;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_MovementPacket_descriptor;
    }

    @java.lang.Override
    public org.y1000.connection.gen.MovementPacket getDefaultInstanceForType() {
      return org.y1000.connection.gen.MovementPacket.getDefaultInstance();
    }

    @java.lang.Override
    public org.y1000.connection.gen.MovementPacket build() {
      org.y1000.connection.gen.MovementPacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.y1000.connection.gen.MovementPacket buildPartial() {
      org.y1000.connection.gen.MovementPacket result = new org.y1000.connection.gen.MovementPacket(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(org.y1000.connection.gen.MovementPacket result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.state_ = state_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.x_ = x_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.y_ = y_;
      }
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.direction_ = direction_;
      }
      if (((from_bitField0_ & 0x00000010) != 0)) {
        result.id_ = id_;
      }
      if (((from_bitField0_ & 0x00000020) != 0)) {
        result.timestamp_ = timestamp_;
      }
      if (((from_bitField0_ & 0x00000040) != 0)) {
        result.sequence_ = sequence_;
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
      if (other instanceof org.y1000.connection.gen.MovementPacket) {
        return mergeFrom((org.y1000.connection.gen.MovementPacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.y1000.connection.gen.MovementPacket other) {
      if (other == org.y1000.connection.gen.MovementPacket.getDefaultInstance()) return this;
      if (other.getState() != 0) {
        setState(other.getState());
      }
      if (other.getX() != 0) {
        setX(other.getX());
      }
      if (other.getY() != 0) {
        setY(other.getY());
      }
      if (other.getDirection() != 0) {
        setDirection(other.getDirection());
      }
      if (other.getId() != 0) {
        setId(other.getId());
      }
      if (other.getTimestamp() != 0L) {
        setTimestamp(other.getTimestamp());
      }
      if (other.getSequence() != 0L) {
        setSequence(other.getSequence());
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
              state_ = input.readInt32();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 16: {
              x_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
            case 24: {
              y_ = input.readInt32();
              bitField0_ |= 0x00000004;
              break;
            } // case 24
            case 32: {
              direction_ = input.readInt32();
              bitField0_ |= 0x00000008;
              break;
            } // case 32
            case 40: {
              id_ = input.readInt32();
              bitField0_ |= 0x00000010;
              break;
            } // case 40
            case 48: {
              timestamp_ = input.readInt64();
              bitField0_ |= 0x00000020;
              break;
            } // case 48
            case 56: {
              sequence_ = input.readInt64();
              bitField0_ |= 0x00000040;
              break;
            } // case 56
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
     * <code>int32 state = 1;</code>
     * @return The state.
     */
    @java.lang.Override
    public int getState() {
      return state_;
    }
    /**
     * <code>int32 state = 1;</code>
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
     * <code>int32 state = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearState() {
      bitField0_ = (bitField0_ & ~0x00000001);
      state_ = 0;
      onChanged();
      return this;
    }

    private int x_ ;
    /**
     * <code>int32 x = 2;</code>
     * @return The x.
     */
    @java.lang.Override
    public int getX() {
      return x_;
    }
    /**
     * <code>int32 x = 2;</code>
     * @param value The x to set.
     * @return This builder for chaining.
     */
    public Builder setX(int value) {

      x_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>int32 x = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearX() {
      bitField0_ = (bitField0_ & ~0x00000002);
      x_ = 0;
      onChanged();
      return this;
    }

    private int y_ ;
    /**
     * <code>int32 y = 3;</code>
     * @return The y.
     */
    @java.lang.Override
    public int getY() {
      return y_;
    }
    /**
     * <code>int32 y = 3;</code>
     * @param value The y to set.
     * @return This builder for chaining.
     */
    public Builder setY(int value) {

      y_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>int32 y = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearY() {
      bitField0_ = (bitField0_ & ~0x00000004);
      y_ = 0;
      onChanged();
      return this;
    }

    private int direction_ ;
    /**
     * <code>int32 direction = 4;</code>
     * @return The direction.
     */
    @java.lang.Override
    public int getDirection() {
      return direction_;
    }
    /**
     * <code>int32 direction = 4;</code>
     * @param value The direction to set.
     * @return This builder for chaining.
     */
    public Builder setDirection(int value) {

      direction_ = value;
      bitField0_ |= 0x00000008;
      onChanged();
      return this;
    }
    /**
     * <code>int32 direction = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearDirection() {
      bitField0_ = (bitField0_ & ~0x00000008);
      direction_ = 0;
      onChanged();
      return this;
    }

    private int id_ ;
    /**
     * <code>int32 id = 5;</code>
     * @return The id.
     */
    @java.lang.Override
    public int getId() {
      return id_;
    }
    /**
     * <code>int32 id = 5;</code>
     * @param value The id to set.
     * @return This builder for chaining.
     */
    public Builder setId(int value) {

      id_ = value;
      bitField0_ |= 0x00000010;
      onChanged();
      return this;
    }
    /**
     * <code>int32 id = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearId() {
      bitField0_ = (bitField0_ & ~0x00000010);
      id_ = 0;
      onChanged();
      return this;
    }

    private long timestamp_ ;
    /**
     * <code>int64 timestamp = 6;</code>
     * @return The timestamp.
     */
    @java.lang.Override
    public long getTimestamp() {
      return timestamp_;
    }
    /**
     * <code>int64 timestamp = 6;</code>
     * @param value The timestamp to set.
     * @return This builder for chaining.
     */
    public Builder setTimestamp(long value) {

      timestamp_ = value;
      bitField0_ |= 0x00000020;
      onChanged();
      return this;
    }
    /**
     * <code>int64 timestamp = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearTimestamp() {
      bitField0_ = (bitField0_ & ~0x00000020);
      timestamp_ = 0L;
      onChanged();
      return this;
    }

    private long sequence_ ;
    /**
     * <code>int64 sequence = 7;</code>
     * @return The sequence.
     */
    @java.lang.Override
    public long getSequence() {
      return sequence_;
    }
    /**
     * <code>int64 sequence = 7;</code>
     * @param value The sequence to set.
     * @return This builder for chaining.
     */
    public Builder setSequence(long value) {

      sequence_ = value;
      bitField0_ |= 0x00000040;
      onChanged();
      return this;
    }
    /**
     * <code>int64 sequence = 7;</code>
     * @return This builder for chaining.
     */
    public Builder clearSequence() {
      bitField0_ = (bitField0_ & ~0x00000040);
      sequence_ = 0L;
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


    // @@protoc_insertion_point(builder_scope:org.y1000.connection.gen.MovementPacket)
  }

  // @@protoc_insertion_point(class_scope:org.y1000.connection.gen.MovementPacket)
  private static final org.y1000.connection.gen.MovementPacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.y1000.connection.gen.MovementPacket();
  }

  public static org.y1000.connection.gen.MovementPacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<MovementPacket>
      PARSER = new com.google.protobuf.AbstractParser<MovementPacket>() {
    @java.lang.Override
    public MovementPacket parsePartialFrom(
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

  public static com.google.protobuf.Parser<MovementPacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<MovementPacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.y1000.connection.gen.MovementPacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
