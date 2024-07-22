package org.y1000.sdb;

import java.util.Set;

public final class CreateDynamicObjectSdbImpl extends AbstractSdbReader implements CreateDynamicObjectSdb {

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
        return names();
    }
}
