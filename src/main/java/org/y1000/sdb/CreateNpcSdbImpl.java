package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class CreateNpcSdbImpl extends AbstractCreateEntitySdb implements CreateNonMonsterSdb {

    public CreateNpcSdbImpl(int realmId) {
        super(makeFileName(realmId));
    }

    public static String makeFileName(int id) {
        return "CreateNpc" + id + ".sdb";
    }

    @Override
    protected String getIdName(String id) {
        return get(id, "NpcName");
    }

    public static void main(String[] args) {

        CreateNpcSdbImpl sdb = new CreateNpcSdbImpl(6);
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.uniqueIds();
        NpcSdb monstersSdb = NonMonsterNpcSdbImpl.Instance;
        Set<String> id = new HashSet<>();
        for (String i: items) {
//            if (!i.startsWith("狐狸") || !"2".equals(sdb.get(i, "Kind")))
//            if (!"2".equals(sdb.get(i, "Kind")))
//                continue;
            System.out.println("----------------------------");
            System.out.println(sdb.get(i, "NpcName"));
            for (String name : names) {
                id.add(monstersSdb.getShape(sdb.get(i, "NpcName")));
                /*if (name.contains("_")) {
                    continue;
                }
                if (!StringUtils.isEmpty(sdb.get(i, name)))
                    System.out.println(name + ": " + sdb.get(i, name));*/
            }
        }
        id.forEach( i -> {
            System.out.println("cp /d/work/godot/y1000/Sprites/" + i+".zip /d/godot_qn/qn_client/sprites");
        });
        /*CreateNpcSdbImpl sdb = new CreateNpcSdbImpl(49);
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.names();
        for (String i: items) {
//            if (!i.startsWith("狐狸") || !"2".equals(sdb.get(i, "Kind")))
//            if (!"2".equals(sdb.get(i, "Kind")))
//                continue;
            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                if (!StringUtils.isEmpty(sdb.get(i, name)))
                    System.out.println(name + ": " + sdb.get(i, name));
            }
        }*/
    }


    private String getValue(String npcName, String key) {
        String id = idNameToId(npcName);
        if (id == null)
            return null;
        return get(id, key);
    }


    @Override
    public Optional<String> getConfig(String idName) {
        String config = getValue(idName, "Config");
        return StringUtils.isEmpty(config) ? Optional.empty() : Optional.of(config);
    }

    @Override
    public boolean containsNpc(String viewName) {
        for (String name : uniqueIds()) {
            if (viewName != null && viewName.equals(get(name, "NpcName")))
                return true;
        }
        return false;
    }


    private Optional<String> getOptionalString(String idName, String key) {
        String config = getValue(idName, key);
        return StringUtils.isEmpty(config) ? Optional.empty() : Optional.of(config);
    }

    @Override
    public Optional<String> getMerchant(String viewName) {
        return getOptionalString(viewName, "Merchant");
    }

    @Override
    public Optional<String> getDialog(String idName) {
        return getOptionalString(idName, "Dialog");
    }

    private String idNameToId(String npcName) {
        for (String id: uniqueIds()) {
            String viewName = getIdName(id);
            if (viewName.equals(npcName)) {
                return id;
            }
        }
        return null;
    }
}

