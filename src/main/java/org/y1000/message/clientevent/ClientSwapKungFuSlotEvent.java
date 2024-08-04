package org.y1000.message.clientevent;

public record ClientSwapKungFuSlotEvent(int page, int slot1, int slot2) implements ClientEvent {

}
