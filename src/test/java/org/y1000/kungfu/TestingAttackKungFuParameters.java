package org.y1000.kungfu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.y1000.kungfu.attack.AttackKungFuParameters;

@Builder
@AllArgsConstructor
public class TestingAttackKungFuParameters implements AttackKungFuParameters {

    public TestingAttackKungFuParameters(int bodyDamage) {
        this.bodyDamage = bodyDamage;
    }

    public TestingAttackKungFuParameters() {
    }

    private int bodyDamage;

    private int recovery;

    private int avoidance;

    private int attackSpeed;

    private int armDamage;
    private int headDamage;
    private int legDamage;
    private int power;
    private int life;
    private int bodyArmor;
    private int headArmor;
    private int armArmor;
    private int legArmor;


    public TestingAttackKungFuParameters setBodyDamage(int bodyDamage) {
        this.bodyDamage = bodyDamage;
        return this;
    }

    public TestingAttackKungFuParameters setLifeToString(int l) {
        this.life = l;
        return this;
    }

    public TestingAttackKungFuParameters setRecovery(int recovery) {
        this.recovery = recovery;
        return this;
    }

    public TestingAttackKungFuParameters setAvoidance(int avoidance) {
        this.avoidance = avoidance;
        return this;
    }

    public TestingAttackKungFuParameters setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
        return this;
    }

    public TestingAttackKungFuParameters setArmDamage(int armDamage) {
        this.armDamage = armDamage;
        return this;
    }

    public TestingAttackKungFuParameters setHeadDamage(int headDamage) {
        this.headDamage = headDamage;
        return this;
    }

    public TestingAttackKungFuParameters setLegDamage(int legDamage) {
        this.legDamage = legDamage;
        return this;
    }

    public TestingAttackKungFuParameters setPowerToSwing(int po) {
        this.power = po;
        return this;
    }

    @Override
    public int powerToSwing() {
        return power;
    }

    @Override
    public int bodyDamage() {
        return bodyDamage;
    }

    @Override
    public int innerPowerToSwing() {
        return 0;
    }

    @Override
    public int recovery() {
        return recovery;
    }

    @Override
    public int avoidance() {
        return avoidance;
    }

    @Override
    public int outerPowerToSwing() {
        return 0;
    }

    @Override
    public int lifeToSwing() {
        return life;
    }

    @Override
    public int attackSpeed() {
        return attackSpeed;
    }

    @Override
    public int armDamage() {
        return armDamage;
    }

    @Override
    public int legDamage() {
        return legDamage;
    }

    @Override
    public int headDamage() {
        return headDamage;
    }
}
