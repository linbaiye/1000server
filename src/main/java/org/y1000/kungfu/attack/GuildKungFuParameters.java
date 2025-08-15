package org.y1000.kungfu.attack;

import lombok.Getter;
import org.y1000.persistence.GuildKungFuPo;

@Getter
public class GuildKungFuParameters extends AbstractKungFuParameters {

    private final GuildKungFuPo provider;

    public GuildKungFuParameters(GuildKungFuPo provider) {
        super(provider);
        this.provider = provider;
    }

    @Override
    public boolean isGuild() {
        return true;
    }
}
