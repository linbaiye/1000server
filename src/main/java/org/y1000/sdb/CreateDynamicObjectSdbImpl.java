package org.y1000.sdb;


import java.util.Optional;
import java.util.Set;

public final class CreateDynamicObjectSdbImpl extends AbstractCSVSdbReader implements CreateDynamicObjectSdb {

    public CreateDynamicObjectSdbImpl(int id) {
        read(makeFileName(id), "utf8");
    }

    public static String makeFileName(int id) {
        return "Setting/CreateDynamicObject" + id + ".sdb";
    }

    @Override
    public int getX(String no) {
        return getInt(no, "X");
    }

    @Override
    public int getY(String no) {
        return getInt(no, "Y");
    }

    @Override
    public String getName(String no) {
        return get(no, "Name");
    }

    @Override
    public Set<String> getNumbers() {
        return uniqueIds();
    }

    @Override
    public Optional<String> getDropItem(String no) {
        return getOptional(no, "DropItem");
    }

    @Override
    public Optional<String> getFirstNo(String name) {
        Set<String> numbers = uniqueIds();
        for (String nu : numbers) {
            if (name.equals(getName(nu))) {
                return Optional.of(nu);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getCallNpc(String no) {
        return getOptional(no, "CallNpc");
    }

    @Override
    public Integer getOffsetX(String no) {
        return getInt(no, "OffsetX");
    }

    @Override
    public Integer getOffsetY(String no) {
        return getInt(no, "OffsetY");
    }

    public static void main(String[] args) {
        CreateDynamicObjectSdbImpl sdb = new CreateDynamicObjectSdbImpl(49);
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.uniqueIds();
        for (String i: items) {

            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                    System.out.println(name + ": " + sdb.get(i, name));
            }
        }
    }
}
