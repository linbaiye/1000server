// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: java.proto

// Protobuf Java Version: 3.25.1
package org.y1000.network.gen;

public interface ShowItemPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:org.y1000.network.gen.ShowItemPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 id = 1;</code>
   * @return The id.
   */
  long getId();

  /**
   * <code>optional int32 number = 4;</code>
   * @return Whether the number field is set.
   */
  boolean hasNumber();
  /**
   * <code>optional int32 number = 4;</code>
   * @return The number.
   */
  int getNumber();

  /**
   * <code>int32 coordinateX = 5;</code>
   * @return The coordinateX.
   */
  int getCoordinateX();

  /**
   * <code>int32 coordinateY = 6;</code>
   * @return The coordinateY.
   */
  int getCoordinateY();

  /**
   * <code>string name = 7;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <code>string name = 7;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <code>int32 color = 8;</code>
   * @return The color.
   */
  int getColor();
}
