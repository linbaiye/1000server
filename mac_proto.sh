#!/bin/bash

rm -f java.proto
cat java.header > java.proto
tail -n +2 src/main/resources/protobuf/message.proto >> java.proto
protoc -I=. --java_out=src/main/java java.proto

rm -f message.proto
cat csharp.header > message.proto
tail -n +2 src/main/resources/protobuf/message.proto >> message.proto
protoc -I=. --csharp_out=../../godot/y1000/Source/Networking/Protobuf message.proto
