#!/bin/bash

rm -f java.proto
cat java.header > java.proto
tail -n +2 src/main/resources/protobuf/message.proto >> java.proto
"D:/work/protobuf/bin/protoc" -I=. --java_out=src/main/java java.proto

rm -f message.proto
cat csharp.header > message.proto
tail -n +2 src/main/resources/protobuf/message.proto >> message.proto
"D:/work/protobuf/bin/protoc" -I=. --csharp_out=../../godot/y1000/Source/Networking/Protobuf message.proto


#rm -f java.proto
#echo 'syntax = "proto3";
#package org.y1000.network.gen;
#option java_package ="org.y1000.network.gen";
#option java_multiple_files = true;' > java.proto
#tail -n +2 src/main/resources/protobuf/account.proto >> java.proto
#"D:/work/protobuf/bin/protoc" -I=. --java_out=src/main/java java.proto

#rm -f account.proto
#echo 'syntax = "proto3";
#package Source.Networking.Protobuf;' > account.proto
#tail -n +2 src/main/resources/protobuf/account.proto >> account.proto
#"D:/work/protobuf/bin/protoc" -I=. --csharp_out=../../godot/y1000/Source/Networking/Protobuf account.proto
