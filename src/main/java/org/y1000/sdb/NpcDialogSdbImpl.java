package org.y1000.sdb;

import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class NpcDialogSdbImpl extends AbstractKeyValuesSdbReader implements NpcDialogSdb {
    private final Map<String, List<String>> dialogs;
    public NpcDialogSdbImpl(String name) {
        Validate.notNull(name);
        dialogs = readKeyValues(path(name));
    }
    public static String path(String name) {
        return "/sdb/NpcDialog/" + name;
    }

    @Override
    public List<String> idleDialogs() {
        return dialogs.getOrDefault("IDLE", Collections.emptyList());
    }
}
