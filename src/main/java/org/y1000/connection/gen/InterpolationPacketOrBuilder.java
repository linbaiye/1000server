// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message.proto

// Protobuf Java Version: 3.25.0-rc2
package org.y1000.connection.gen;

public interface InterpolationPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:org.y1000.connection.gen.InterpolationPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 id = 1;</code>
   * @return The id.
   */
  long getId();

  /**
   * <code>int32 state = 2;</code>
   * @return The state.
   */
  int getState();

  /**
   * <code>int64 timestamp = 3;</code>
   * @return The timestamp.
   */
  long getTimestamp();

  /**
   * <code>int64 stateStart = 4;</code>
   * @return The stateStart.
   */
  long getStateStart();

  /**
   * <code>int64 interpolationStart = 5;</code>
   * @return The interpolationStart.
   */
  long getInterpolationStart();

  /**
   * <code>int32 duration = 6;</code>
   * @return The duration.
   */
  int getDuration();

  /**
   * <code>int32 direction = 7;</code>
   * @return The direction.
   */
  int getDirection();

  /**
   * <code>int32 x = 8;</code>
   * @return The x.
   */
  int getX();

  /**
   * <code>int32 y = 9;</code>
   * @return The y.
   */
  int getY();
}