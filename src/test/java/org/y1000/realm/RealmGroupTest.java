package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.realm.event.RealmEvent;
import org.y1000.sdb.CreateGateSdbImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RealmGroupTest {

    private RealmGroup realmGroup;

    private List<Realm> realmList;

    @BeforeEach
    void setUp() {
        realmList = new ArrayList<>();
        realmGroup = new RealmGroup(realmList);
    }

    @Test
    void run() {
    }

    @Test
    void handle() {
    }
}