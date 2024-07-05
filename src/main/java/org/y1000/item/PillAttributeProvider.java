package org.y1000.item;

public interface PillAttributeProvider {

    int useInterval();

    int useCount();

    int power();

    int innerPower();

    int outerPower();

    int life();

    int headLife();

    int armLife();

    int legLife();

    /*
    Name,Type,UseInterval,UseCount,StillInterval,eEnergy,eInPower,eOutPower,eMagic,eLife,eHeadLife,eArmLife,eLegLife,DamageBody,DamageHead,DamageArm,DamageLeg,ArmorBody,ArmorHead,ArmorArm,ArmorLeg,AttackSpeed,Avoid,Recovery,Accuracy,KeepRecovery,LightDark,

     */
}
