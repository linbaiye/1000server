// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message.proto

// Protobuf Java Version: 3.25.1
package org.y1000.connection.gen;

/**
 * Protobuf type {@code org.y1000.connection.gen.ShowCreaturePacket}
 */
public final class ShowCreaturePacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:org.y1000.connection.gen.ShowCreaturePacket)
    ShowCreaturePacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ShowCreaturePacket.newBuilder() to construct.
  private ShowCreaturePacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ShowCreaturePacket() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ShowCreaturePacket();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_ShowCreaturePacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_ShowCreaturePacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.y1000.connection.gen.ShowCreaturePacket.class, org.y1000.connection.gen.ShowCreaturePacket.Builder.class);
  }

  private int bitField0_;
  public static final int MOVEMENT_FIELD_NUMBER = 1;
  private org.y1000.connection.gen.MovementPacket movement_;
  /**
   * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
   * @return Whether the movement field is set.
   */
  @java.lang.Override
  public boolean hasMovement() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
   * @return The movement.
   */
  @java.lang.Override
  public org.y1000.connection.gen.MovementPacket getMovement() {
    return movement_ == null ? org.y1000.connection.gen.MovementPacket.getDefaultInstance() : movement_;
  }
  /**
   * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
   */
  @java.lang.Override
  public org.y1000.connection.gen.MovementPacketOrBuilder getMovementOrBuilder() {
    return movement_ == null ? org.y1000.connection.gen.MovementPacket.getDefaultInstance() : movement_;
  }

  public static final int CREATURETYPE_FIELD_NUMBER = 2;
  private int creatureType_ = 0;
  /**
   * <code>int32 creatureType = 2;</code>
   * @return The creatureType.
   */
  @java.lang.Override
  public int getCreatureType() {
    return creatureType_;
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
    if (((bitField0_ & 0x00000001) != 0)) {
      output.writeMessage(1, getMovement());
    }
    if (creatureType_ != 0) {
      output.writeInt32(2, creatureType_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getMovement());
    }
    if (creatureType_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, creatureType_);
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
    if (!(obj instanceof org.y1000.connection.gen.ShowCreaturePacket)) {
      return super.equals(obj);
    }
    org.y1000.connection.gen.ShowCreaturePacket other = (org.y1000.connection.gen.ShowCreaturePacket) obj;

    if (hasMovement() != other.hasMovement()) return false;
    if (hasMovement()) {
      if (!getMovement()
          .equals(other.getMovement())) return false;
    }
    if (getCreatureType()
        != other.getCreatureType()) return false;
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
    if (hasMovement()) {
      hash = (37 * hash) + MOVEMENT_FIELD_NUMBER;
      hash = (53 * hash) + getMovement().hashCode();
    }
    hash = (37 * hash) + CREATURETYPE_FIELD_NUMBER;
    hash = (53 * hash) + getCreatureType();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static org.y1000.connection.gen.ShowCreaturePacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static org.y1000.connection.gen.ShowCreaturePacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.y1000.connection.gen.ShowCreaturePacket parseFrom(
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
  public static Builder newBuilder(org.y1000.connection.gen.ShowCreaturePacket prototype) {
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
   * Protobuf type {@code org.y1000.connection.gen.ShowCreaturePacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:org.y1000.connection.gen.ShowCreaturePacket)
      org.y1000.connection.gen.ShowCreaturePacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_ShowCreaturePacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_ShowCreaturePacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.y1000.connection.gen.ShowCreaturePacket.class, org.y1000.connection.gen.ShowCreaturePacket.Builder.class);
    }

    // Construct using org.y1000.connection.gen.ShowCreaturePacket.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
        getMovementFieldBuilder();
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      movement_ = null;
      if (movementBuilder_ != null) {
        movementBuilder_.dispose();
        movementBuilder_ = null;
      }
      creatureType_ = 0;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.y1000.connection.gen.Message.internal_static_org_y1000_connection_gen_ShowCreaturePacket_descriptor;
    }

    @java.lang.Override
    public org.y1000.connection.gen.ShowCreaturePacket getDefaultInstanceForType() {
      return org.y1000.connection.gen.ShowCreaturePacket.getDefaultInstance();
    }

    @java.lang.Override
    public org.y1000.connection.gen.ShowCreaturePacket build() {
      org.y1000.connection.gen.ShowCreaturePacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.y1000.connection.gen.ShowCreaturePacket buildPartial() {
      org.y1000.connection.gen.ShowCreaturePacket result = new org.y1000.connection.gen.ShowCreaturePacket(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(org.y1000.connection.gen.ShowCreaturePacket result) {
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.movement_ = movementBuilder_ == null
            ? movement_
            : movementBuilder_.build();
        to_bitField0_ |= 0x00000001;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.creatureType_ = creatureType_;
      }
      result.bitField0_ |= to_bitField0_;
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
      if (other instanceof org.y1000.connection.gen.ShowCreaturePacket) {
        return mergeFrom((org.y1000.connection.gen.ShowCreaturePacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.y1000.connection.gen.ShowCreaturePacket other) {
      if (other == org.y1000.connection.gen.ShowCreaturePacket.getDefaultInstance()) return this;
      if (other.hasMovement()) {
        mergeMovement(other.getMovement());
      }
      if (other.getCreatureType() != 0) {
        setCreatureType(other.getCreatureType());
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
            case 10: {
              input.readMessage(
                  getMovementFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 16: {
              creatureType_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
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

    private org.y1000.connection.gen.MovementPacket movement_;
    private com.google.protobuf.SingleFieldBuilderV3<
        org.y1000.connection.gen.MovementPacket, org.y1000.connection.gen.MovementPacket.Builder, org.y1000.connection.gen.MovementPacketOrBuilder> movementBuilder_;
    /**
     * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
     * @return Whether the movement field is set.
     */
    public boolean hasMovement() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
     * @return The movement.
     */
    public org.y1000.connection.gen.MovementPacket getMovement() {
      if (movementBuilder_ == null) {
        return movement_ == null ? org.y1000.connection.gen.MovementPacket.getDefaultInstance() : movement_;
      } else {
        return movementBuilder_.getMessage();
      }
    }
    /**
     * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
     */
    public Builder setMovement(org.y1000.connection.gen.MovementPacket value) {
      if (movementBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        movement_ = value;
      } else {
        movementBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
     */
    public Builder setMovement(
        org.y1000.connection.gen.MovementPacket.Builder builderForValue) {
      if (movementBuilder_ == null) {
        movement_ = builderForValue.build();
      } else {
        movementBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
     */
    public Builder mergeMovement(org.y1000.connection.gen.MovementPacket value) {
      if (movementBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0) &&
          movement_ != null &&
          movement_ != org.y1000.connection.gen.MovementPacket.getDefaultInstance()) {
          getMovementBuilder().mergeFrom(value);
        } else {
          movement_ = value;
        }
      } else {
        movementBuilder_.mergeFrom(value);
      }
      if (movement_ != null) {
        bitField0_ |= 0x00000001;
        onChanged();
      }
      return this;
    }
    /**
     * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
     */
    public Builder clearMovement() {
      bitField0_ = (bitField0_ & ~0x00000001);
      movement_ = null;
      if (movementBuilder_ != null) {
        movementBuilder_.dispose();
        movementBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
     */
    public org.y1000.connection.gen.MovementPacket.Builder getMovementBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getMovementFieldBuilder().getBuilder();
    }
    /**
     * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
     */
    public org.y1000.connection.gen.MovementPacketOrBuilder getMovementOrBuilder() {
      if (movementBuilder_ != null) {
        return movementBuilder_.getMessageOrBuilder();
      } else {
        return movement_ == null ?
            org.y1000.connection.gen.MovementPacket.getDefaultInstance() : movement_;
      }
    }
    /**
     * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        org.y1000.connection.gen.MovementPacket, org.y1000.connection.gen.MovementPacket.Builder, org.y1000.connection.gen.MovementPacketOrBuilder> 
        getMovementFieldBuilder() {
      if (movementBuilder_ == null) {
        movementBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            org.y1000.connection.gen.MovementPacket, org.y1000.connection.gen.MovementPacket.Builder, org.y1000.connection.gen.MovementPacketOrBuilder>(
                getMovement(),
                getParentForChildren(),
                isClean());
        movement_ = null;
      }
      return movementBuilder_;
    }

    private int creatureType_ ;
    /**
     * <code>int32 creatureType = 2;</code>
     * @return The creatureType.
     */
    @java.lang.Override
    public int getCreatureType() {
      return creatureType_;
    }
    /**
     * <code>int32 creatureType = 2;</code>
     * @param value The creatureType to set.
     * @return This builder for chaining.
     */
    public Builder setCreatureType(int value) {

      creatureType_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>int32 creatureType = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearCreatureType() {
      bitField0_ = (bitField0_ & ~0x00000002);
      creatureType_ = 0;
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


    // @@protoc_insertion_point(builder_scope:org.y1000.connection.gen.ShowCreaturePacket)
  }

  // @@protoc_insertion_point(class_scope:org.y1000.connection.gen.ShowCreaturePacket)
  private static final org.y1000.connection.gen.ShowCreaturePacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.y1000.connection.gen.ShowCreaturePacket();
  }

  public static org.y1000.connection.gen.ShowCreaturePacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ShowCreaturePacket>
      PARSER = new com.google.protobuf.AbstractParser<ShowCreaturePacket>() {
    @java.lang.Override
    public ShowCreaturePacket parsePartialFrom(
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

  public static com.google.protobuf.Parser<ShowCreaturePacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ShowCreaturePacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.y1000.connection.gen.ShowCreaturePacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
