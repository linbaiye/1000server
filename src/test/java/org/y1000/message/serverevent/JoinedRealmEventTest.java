package org.y1000.message.serverevent;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.AbstractPlayerUnitTestFixture;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.LoginPacket;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JoinedRealmEventTest extends AbstractPlayerUnitTestFixture {


    @Test
    void toPacket() {
        PlayerImpl player = playerBuilder().build();
        RealmMap map = Mockito.mock(RealmMap.class);
        when(map.objectFile()).thenReturn("obj");
        when(map.roofFile()).thenReturn("roof");
        when(map.tileFile()).thenReturn("tile");
        when(map.mapFile()).thenReturn("map");
        Realm realm = Mockito.mock(Realm.class);
        when(realm.map()).thenReturn(map);
        when(realm.bgm()).thenReturn("bgm");
        when(realm.name()).thenReturn("test");
        LoginPacket loginPacket = new JoinedRealmEvent(player, player.coordinate(), player.inventory(), realm).toPacket().getLoginPacket();
        assertEquals("bgm", loginPacket.getTeleport().getBgm());
        assertEquals("map", loginPacket.getTeleport().getMap());
        assertEquals("obj", loginPacket.getTeleport().getObj());
        assertEquals("roof", loginPacket.getTeleport().getRof());
        assertEquals("tile", loginPacket.getTeleport().getTile());
        assertEquals("test", loginPacket.getTeleport().getRealm());
    }
}