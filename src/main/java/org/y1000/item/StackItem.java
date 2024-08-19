package org.y1000.item;

import org.apache.commons.lang3.Validate;

import java.util.Optional;

public record StackItem(Item item, long number) implements Item {

    public StackItem {
        Validate.isTrue(number >= 0);
        Validate.notNull(item);
    }

    @Override
    public int color() {
        return item.color();
    }

    private static final long MAX_NUMBER = 100000000000L;

    @Override
    public String name() {
        return item.name();
    }

    public static long capacity() {
        return MAX_NUMBER;
    }

    public <T extends Item> Optional<T> origin(Class<T> type) {
        return type.isAssignableFrom(item.getClass()) ? Optional.of(type.cast(item)) : Optional.empty();
    }

    @Override
    public ItemType itemType() {
        return item.itemType();
    }

    @Override
    public String description() {
        return item.description();
    }

    public boolean hasEnough(long n) {
        return number >= n;
    }

    public StackItem increase(long n) {
        Validate.isTrue(n > 0);
        return hasMoreSpace(n) ? new StackItem(item, number() + n) : this;
    }

    public boolean hasMoreSpace(long more) {
        return number + more <= MAX_NUMBER;
    }

    public long number() {
        return number;
    }

    @Override
    public Optional<String> dropSound() {
        return item.dropSound();
    }

    @Override
    public Optional<String> eventSound() {
        return item.eventSound();
    }

    public boolean containsSameItem(Item item) {
        return item != null && name().equals(item.name());
    }

    public StackItem decrease(long n) {
        Validate.isTrue(n > 0);
        return hasEnough(n) ? new StackItem(item, number() - n) : new StackItem(item, 0);
    }

    @Override
    public String toString() {
        return item.name() + ":" + number;
    }
}

