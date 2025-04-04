// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: java.proto

// Protobuf Java Version: 3.25.1
package org.y1000.network.gen;

/**
 * Protobuf type {@code org.y1000.network.gen.UpdateDynamicObjectPacket}
 */
public final class UpdateDynamicObjectPacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:org.y1000.network.gen.UpdateDynamicObjectPacket)
    UpdateDynamicObjectPacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use UpdateDynamicObjectPacket.newBuilder() to construct.
  private UpdateDynamicObjectPacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private UpdateDynamicObjectPacket() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new UpdateDynamicObjectPacket();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_UpdateDynamicObjectPacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_UpdateDynamicObjectPacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.y1000.network.gen.UpdateDynamicObjectPacket.class, org.y1000.network.gen.UpdateDynamicObjectPacket.Builder.class);
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

  public static final int START_FIELD_NUMBER = 2;
  private int start_ = 0;
  /**
   * <code>int32 start = 2;</code>
   * @return The start.
   */
  @java.lang.Override
  public int getStart() {
    return start_;
  }

  public static final int END_FIELD_NUMBER = 3;
  private int end_ = 0;
  /**
   * <code>int32 end = 3;</code>
   * @return The end.
   */
  @java.lang.Override
  public int getEnd() {
    return end_;
  }

  public static final int LOOP_FIELD_NUMBER = 4;
  private boolean loop_ = false;
  /**
   * <code>bool loop = 4;</code>
   * @return The loop.
   */
  @java.lang.Override
  public boolean getLoop() {
    return loop_;
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
    if (start_ != 0) {
      output.writeInt32(2, start_);
    }
    if (end_ != 0) {
      output.writeInt32(3, end_);
    }
    if (loop_ != false) {
      output.writeBool(4, loop_);
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
    if (start_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, start_);
    }
    if (end_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, end_);
    }
    if (loop_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(4, loop_);
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
    if (!(obj instanceof org.y1000.network.gen.UpdateDynamicObjectPacket)) {
      return super.equals(obj);
    }
    org.y1000.network.gen.UpdateDynamicObjectPacket other = (org.y1000.network.gen.UpdateDynamicObjectPacket) obj;

    if (getId()
        != other.getId()) return false;
    if (getStart()
        != other.getStart()) return false;
    if (getEnd()
        != other.getEnd()) return false;
    if (getLoop()
        != other.getLoop()) return false;
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
    hash = (37 * hash) + START_FIELD_NUMBER;
    hash = (53 * hash) + getStart();
    hash = (37 * hash) + END_FIELD_NUMBER;
    hash = (53 * hash) + getEnd();
    hash = (37 * hash) + LOOP_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getLoop());
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.network.gen.UpdateDynamicObjectPacket parseFrom(
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
  public static Builder newBuilder(org.y1000.network.gen.UpdateDynamicObjectPacket prototype) {
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
   * Protobuf type {@code org.y1000.network.gen.UpdateDynamicObjectPacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:org.y1000.network.gen.UpdateDynamicObjectPacket)
      org.y1000.network.gen.UpdateDynamicObjectPacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_UpdateDynamicObjectPacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_UpdateDynamicObjectPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.y1000.network.gen.UpdateDynamicObjectPacket.class, org.y1000.network.gen.UpdateDynamicObjectPacket.Builder.class);
    }

    // Construct using org.y1000.network.gen.UpdateDynamicObjectPacket.newBuilder()
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
      start_ = 0;
      end_ = 0;
      loop_ = false;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.y1000.network.gen.Java.internal_static_org_y1000_network_gen_UpdateDynamicObjectPacket_descriptor;
    }

    @java.lang.Override
    public org.y1000.network.gen.UpdateDynamicObjectPacket getDefaultInstanceForType() {
      return org.y1000.network.gen.UpdateDynamicObjectPacket.getDefaultInstance();
    }

    @java.lang.Override
    public org.y1000.network.gen.UpdateDynamicObjectPacket build() {
      org.y1000.network.gen.UpdateDynamicObjectPacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.y1000.network.gen.UpdateDynamicObjectPacket buildPartial() {
      org.y1000.network.gen.UpdateDynamicObjectPacket result = new org.y1000.network.gen.UpdateDynamicObjectPacket(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(org.y1000.network.gen.UpdateDynamicObjectPacket result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.id_ = id_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.start_ = start_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.end_ = end_;
      }
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.loop_ = loop_;
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
      if (other instanceof org.y1000.network.gen.UpdateDynamicObjectPacket) {
        return mergeFrom((org.y1000.network.gen.UpdateDynamicObjectPacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.y1000.network.gen.UpdateDynamicObjectPacket other) {
      if (other == org.y1000.network.gen.UpdateDynamicObjectPacket.getDefaultInstance()) return this;
      if (other.getId() != 0L) {
        setId(other.getId());
      }
      if (other.getStart() != 0) {
        setStart(other.getStart());
      }
      if (other.getEnd() != 0) {
        setEnd(other.getEnd());
      }
      if (other.getLoop() != false) {
        setLoop(other.getLoop());
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
              start_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
            case 24: {
              end_ = input.readInt32();
              bitField0_ |= 0x00000004;
              break;
            } // case 24
            case 32: {
              loop_ = input.readBool();
              bitField0_ |= 0x00000008;
              break;
            } // case 32
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

    private int start_ ;
    /**
     * <code>int32 start = 2;</code>
     * @return The start.
     */
    @java.lang.Override
    public int getStart() {
      return start_;
    }
    /**
     * <code>int32 start = 2;</code>
     * @param value The start to set.
     * @return This builder for chaining.
     */
    public Builder setStart(int value) {

      start_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>int32 start = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearStart() {
      bitField0_ = (bitField0_ & ~0x00000002);
      start_ = 0;
      onChanged();
      return this;
    }

    private int end_ ;
    /**
     * <code>int32 end = 3;</code>
     * @return The end.
     */
    @java.lang.Override
    public int getEnd() {
      return end_;
    }
    /**
     * <code>int32 end = 3;</code>
     * @param value The end to set.
     * @return This builder for chaining.
     */
    public Builder setEnd(int value) {

      end_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>int32 end = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearEnd() {
      bitField0_ = (bitField0_ & ~0x00000004);
      end_ = 0;
      onChanged();
      return this;
    }

    private boolean loop_ ;
    /**
     * <code>bool loop = 4;</code>
     * @return The loop.
     */
    @java.lang.Override
    public boolean getLoop() {
      return loop_;
    }
    /**
     * <code>bool loop = 4;</code>
     * @param value The loop to set.
     * @return This builder for chaining.
     */
    public Builder setLoop(boolean value) {

      loop_ = value;
      bitField0_ |= 0x00000008;
      onChanged();
      return this;
    }
    /**
     * <code>bool loop = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearLoop() {
      bitField0_ = (bitField0_ & ~0x00000008);
      loop_ = false;
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


    // @@protoc_insertion_point(builder_scope:org.y1000.network.gen.UpdateDynamicObjectPacket)
  }

  // @@protoc_insertion_point(class_scope:org.y1000.network.gen.UpdateDynamicObjectPacket)
  private static final org.y1000.network.gen.UpdateDynamicObjectPacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.y1000.network.gen.UpdateDynamicObjectPacket();
  }

  public static org.y1000.network.gen.UpdateDynamicObjectPacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<UpdateDynamicObjectPacket>
      PARSER = new com.google.protobuf.AbstractParser<UpdateDynamicObjectPacket>() {
    @java.lang.Override
    public UpdateDynamicObjectPacket parsePartialFrom(
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

  public static com.google.protobuf.Parser<UpdateDynamicObjectPacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<UpdateDynamicObjectPacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.y1000.network.gen.UpdateDynamicObjectPacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

