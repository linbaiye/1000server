package org.y1000.message.input;

import org.y1000.message.ValueEnum;


public record ClientOperateBankEvent(Operation operation, long bankerId, int fromSlot, int toSlot, long number)
        implements ClientEvent {

    public enum Operation implements ValueEnum  {
        OPEN(1),
        INVENTORY_TO_BANK(2),
        BANK_TO_INVENTORY(3),
        UNLOCK_SLOTS(4),
        CLOSE(5),
        ;

        private final int v;

        Operation(int v) {
            this.v = v;
        }
        @Override
        public int value() {
            return v;
        }
        public static Operation fromValue(int v) {
            return ValueEnum.fromValueOrThrow(values(), v);
        }
    }
    public static ClientOperateBankEvent open(long bankerId) {
        return new ClientOperateBankEvent(Operation.OPEN, bankerId, 0, 0, 0);
    }

    public static ClientOperateBankEvent unlock(int invSlot) {
        return new ClientOperateBankEvent(Operation.UNLOCK_SLOTS, 0, invSlot, 0, 0);
    }

    public boolean isOpen() {
        return operation == Operation.OPEN;
    }

    public boolean isClose() {
        return operation == Operation.CLOSE;
    }

    public boolean isUnlock() {
        return operation == Operation.UNLOCK_SLOTS;
    }
    public boolean isInventoryToBank() {
        return operation == Operation.INVENTORY_TO_BANK;
    }

    public boolean isBankToInventory() {
        return operation == Operation.BANK_TO_INVENTORY;
    }


}
