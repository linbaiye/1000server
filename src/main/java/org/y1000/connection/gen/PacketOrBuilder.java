// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message.proto

// Protobuf Java Version: 3.25.0-rc2
package org.y1000.connection.gen;

public interface PacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:org.y1000.connection.gen.Packet)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.org.y1000.connection.gen.MovementPacket movementPacket = 1;</code>
   * @return Whether the movementPacket field is set.
   */
  boolean hasMovementPacket();
  /**
   * <code>.org.y1000.connection.gen.MovementPacket movementPacket = 1;</code>
   * @return The movementPacket.
   */
  org.y1000.connection.gen.MovementPacket getMovementPacket();
  /**
   * <code>.org.y1000.connection.gen.MovementPacket movementPacket = 1;</code>
   */
  org.y1000.connection.gen.MovementPacketOrBuilder getMovementPacketOrBuilder();

  /**
   * <code>.org.y1000.connection.gen.ShowCreaturePacket showCreaturePacket = 2;</code>
   * @return Whether the showCreaturePacket field is set.
   */
  boolean hasShowCreaturePacket();
  /**
   * <code>.org.y1000.connection.gen.ShowCreaturePacket showCreaturePacket = 2;</code>
   * @return The showCreaturePacket.
   */
  org.y1000.connection.gen.ShowCreaturePacket getShowCreaturePacket();
  /**
   * <code>.org.y1000.connection.gen.ShowCreaturePacket showCreaturePacket = 2;</code>
   */
  org.y1000.connection.gen.ShowCreaturePacketOrBuilder getShowCreaturePacketOrBuilder();

  /**
   * <code>.org.y1000.connection.gen.InputPacket inputPacket = 3;</code>
   * @return Whether the inputPacket field is set.
   */
  boolean hasInputPacket();
  /**
   * <code>.org.y1000.connection.gen.InputPacket inputPacket = 3;</code>
   * @return The inputPacket.
   */
  org.y1000.connection.gen.InputPacket getInputPacket();
  /**
   * <code>.org.y1000.connection.gen.InputPacket inputPacket = 3;</code>
   */
  org.y1000.connection.gen.InputPacketOrBuilder getInputPacketOrBuilder();

  /**
   * <code>.org.y1000.connection.gen.LoginPacket loginPacket = 4;</code>
   * @return Whether the loginPacket field is set.
   */
  boolean hasLoginPacket();
  /**
   * <code>.org.y1000.connection.gen.LoginPacket loginPacket = 4;</code>
   * @return The loginPacket.
   */
  org.y1000.connection.gen.LoginPacket getLoginPacket();
  /**
   * <code>.org.y1000.connection.gen.LoginPacket loginPacket = 4;</code>
   */
  org.y1000.connection.gen.LoginPacketOrBuilder getLoginPacketOrBuilder();

  /**
   * <code>.org.y1000.connection.gen.InterpolationsPacket interpolations = 5;</code>
   * @return Whether the interpolations field is set.
   */
  boolean hasInterpolations();
  /**
   * <code>.org.y1000.connection.gen.InterpolationsPacket interpolations = 5;</code>
   * @return The interpolations.
   */
  org.y1000.connection.gen.InterpolationsPacket getInterpolations();
  /**
   * <code>.org.y1000.connection.gen.InterpolationsPacket interpolations = 5;</code>
   */
  org.y1000.connection.gen.InterpolationsPacketOrBuilder getInterpolationsOrBuilder();

  org.y1000.connection.gen.Packet.TypedPacketCase getTypedPacketCase();
}
