// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: java.proto

// Protobuf Java Version: 3.25.1
package org.y1000.network.gen;

public interface ClientAttackResponsePacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:org.y1000.network.gen.ClientAttackResponsePacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 sequence = 1;</code>
   * @return The sequence.
   */
  long getSequence();

  /**
   * <code>bool accepted = 2;</code>
   * @return The accepted.
   */
  boolean getAccepted();

  /**
   * <code>optional int32 backToState = 3;</code>
   * @return Whether the backToState field is set.
   */
  boolean hasBackToState();
  /**
   * <code>optional int32 backToState = 3;</code>
   * @return The backToState.
   */
  int getBackToState();
}
