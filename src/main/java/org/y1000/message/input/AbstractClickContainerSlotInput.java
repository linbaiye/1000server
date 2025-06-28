package org.y1000.message.input;

/**
 * When a click event happened to inventory or kung fu book.
 */
public abstract class AbstractClickContainerSlotInput implements SelfHandleInput {

    protected final ClickType clickType;
    protected final int slot;

    public AbstractClickContainerSlotInput(ClickType clickType, int slot) {
        this.clickType = clickType;
        this.slot = slot;
    }

    public enum ClickType {
        // 1
        LeftClick,
        // 2
        LeftDoubleClick,
        // 3
        RightClick,
        ;
        public static ClickType type(int v) {
            if (v == 1)
                return ClickType.LeftClick;
            else if (v == 2)
                return  ClickType.LeftDoubleClick;
            else if (v == 3)
                return  ClickType.RightClick;
            throw new IllegalArgumentException("Unknown value " + v);
        }
    }
}
