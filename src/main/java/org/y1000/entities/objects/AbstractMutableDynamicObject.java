package org.y1000.entities.objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.AbstractActiveEntity;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractMutableDynamicObject extends AbstractActiveEntity implements DynamicObject {

    private final Coordinate coordinate;

    private final RealmMap realmMap;
    private final DynamicObjectSdb dynamicObjectSdb;
    private final String idName;

    private final Animation[] animations;
    private final Set<Coordinate> occupyingCoordinates;
    private int animationIndex;
    private int animationTotalDuration;
    private int animationElapsedDuration;
    private static final int FRAME_DURATION = 200;



    public AbstractMutableDynamicObject(long id, Coordinate coordinate, RealmMap realmMap, DynamicObjectSdb dynamicObjectSdb, String idName, Animation[] animations) {
        super(id);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);
        Validate.notNull(dynamicObjectSdb);
        Validate.notNull(idName);
        this.coordinate = coordinate;
        this.realmMap = realmMap;
        this.dynamicObjectSdb = dynamicObjectSdb;
        this.idName = idName;
        this.animations = animations;
        this.animationIndex = 0;
        this.animationTotalDuration = animations[0].total() * FRAME_DURATION;
        this.animationElapsedDuration = 0;
        occupyingCoordinates = parseCoordinates(idName, coordinate, dynamicObjectSdb);
        realmMap.occupy(this);
    }

    private static Set<Coordinate> parseCoordinates(String idName, Coordinate coordinate, DynamicObjectSdb sdb) {
        String guardPos = sdb.getGuardPos(idName);
        if (StringUtils.isEmpty(guardPos)) {
            return Set.of(coordinate);
        }
        String[] tokens = guardPos.split(":");
        if (tokens.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid guardPos: " + guardPos + ", idName:" + idName);
        }
        Coordinate[] guardCoordinates = new Coordinate[tokens.length / 2 + 1];
        guardCoordinates[0] = coordinate;
        for (int i = 0, j = 1; i < tokens.length / 2; i++, j++) {
            int x = Integer.parseInt(tokens[i * 2]);
            int y = Integer.parseInt(tokens[i * 2 + 1]);
            guardCoordinates[j] = coordinate.move(x, y);
        }
        return Set.of(guardCoordinates);
    }

    static Animation[] parseAnimations(String idName, DynamicObjectSdb sdb, Function<Integer, Boolean> loopDecider) {
        try {
            int s0 = Integer.parseInt(sdb.getSStep0(idName));
            int e0 = Integer.parseInt(sdb.getEStep0(idName));
            int s1 = Integer.parseInt(sdb.getSStep1(idName));
            int e1 = Integer.parseInt(sdb.getEStep1(idName));
            String sStep2 = sdb.getSStep2(idName);
            int s2 = StringUtils.isNotEmpty(sStep2) ? Integer.parseInt(sStep2) : e1;
            String eStep2 = sdb.getEStep2(idName);
            int e2 = StringUtils.isNotEmpty(eStep2) ? Integer.parseInt(eStep2) : s2;
            return new Animation[]{new Animation(s0, e0, loopDecider.apply(0)), new Animation(s1, e1, loopDecider.apply(1)), new Animation(s2, e2, loopDecider.apply(2))};
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid animation for " + idName, e);
        }
    }

    static Animation[] parseAnimationFrames(String idName, DynamicObjectSdb sdb) {
        return parseAnimations(idName, sdb, integer -> integer != 0 && integer != 1);
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    protected void changeAnimation(int index, int duration) {
        animationIndex = index;
        animationTotalDuration = duration;
        animationElapsedDuration = 0;
        emitEvent(new UpdateDynamicObjectEvent(this));
    }

    protected int getAnimationIndex() {
        return animationIndex;
    }

    protected void changeAnimation(int index) {
        changeAnimation(index, animations[index].total() * FRAME_DURATION);
    }

    public Animation currentAnimation() {
        return animations[animationIndex];
    }

    protected DynamicObjectSdb dynamicObjectSdb() {
        return dynamicObjectSdb;
    }

    protected void updateAnimation(int delta) {
        animationElapsedDuration += delta;
        if (animationElapsedDuration >= animationTotalDuration) {
            onAnimationDone();
        }
    }

    protected int animationElapsedDuration() {
        return animationElapsedDuration;
    }

    protected abstract void onAnimationDone();

    @Override
    public Set<Coordinate> occupyingCoordinates() {
        return occupyingCoordinates;
    }



    public String shape() {
        return dynamicObjectSdb.getShape(idName);
    }

    public Optional<String> viewName() {
        return dynamicObjectSdb.getViewName(idName);
    }

    public RealmMap realmMap() {
        return realmMap;
    }

    @Override
    public String idName() {
        return idName;
    }
}
