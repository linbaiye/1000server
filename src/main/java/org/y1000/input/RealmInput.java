package org.y1000.input;

import org.y1000.util.ValueEnum;

public record RealmInput(Type type) {
    public enum Type implements ValueEnum {
        GetNpcCoordinates(1);
        private final int v;

        Type(int v) {
            this.v = v;
        }
        @Override
        public int value() {
            return v;
        }
    }

    public static RealmInput of(int t) {
        return new RealmInput(ValueEnum.getTypeOrThrow(Type.values(), t));
    }
}
