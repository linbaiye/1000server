package org.y1000.realm;


import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.projectile.Projectile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public final class ProjectileManager {
    private final Set<Projectile> projectiles;


    public ProjectileManager() {
        this.projectiles = new HashSet<>();
    }

    public void add(Projectile projectile) {
        projectiles.add(projectile);
    }

    public void update(long delta) {
        for (Iterator<Projectile> iterator = projectiles.iterator(); iterator.hasNext();) {
            Projectile projectile = iterator.next();
            try {
                if (projectile.update((int) delta)) {
                    iterator.remove();
                }
            } catch (Exception e) {
                iterator.remove();
                log.error("Exception when updating {}.", projectile, e);
            }
        }
    }
}
