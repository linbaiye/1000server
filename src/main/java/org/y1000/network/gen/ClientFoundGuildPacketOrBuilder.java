// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: java.proto

// Protobuf Java Version: 3.25.1
package org.y1000.network.gen;

public interface ClientFoundGuildPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:org.y1000.network.gen.ClientFoundGuildPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string name = 1;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <code>string name = 1;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <code>int32 x = 2;</code>
   * @return The x.
   */
  int getX();

  /**
   * <code>int32 y = 3;</code>
   * @return The y.
   */
  int getY();

  /**
   * <code>int32 inventorySlot = 4;</code>
   * @return The inventorySlot.
   */
  int getInventorySlot();
}
