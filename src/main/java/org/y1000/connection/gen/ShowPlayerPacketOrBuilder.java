// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message.proto

// Protobuf Java Version: 3.25.1
package org.y1000.connection.gen;

public interface ShowPlayerPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:org.y1000.connection.gen.ShowPlayerPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
   * @return Whether the movement field is set.
   */
  boolean hasMovement();
  /**
   * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
   * @return The movement.
   */
  org.y1000.connection.gen.MovementPacket getMovement();
  /**
   * <code>.org.y1000.connection.gen.MovementPacket movement = 1;</code>
   */
  org.y1000.connection.gen.MovementPacketOrBuilder getMovementOrBuilder();

  /**
   * <code>int64 id = 2;</code>
   * @return The id.
   */
  long getId();
}