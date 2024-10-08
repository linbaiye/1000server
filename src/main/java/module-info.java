module Server {
    requires static lombok;
    requires org.apache.commons.lang3;
    requires org.slf4j;
    requires io.netty.transport;
    requires com.google.protobuf;
    requires io.netty.buffer;
    requires io.netty.codec;
    requires io.netty.handler;
    requires io.netty.codec.http;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires com.fasterxml.jackson.databind;

    opens org.y1000.persistence;
    opens org.y1000.account;
    opens org.y1000.management;
}

