package org.y1000.entities.players;

import org.y1000.exp.Experience;
import org.y1000.exp.ExperienceUtil;
import java.time.LocalDateTime;

public final class YinYang {
    private final Experience yin;
    private final Experience yang;
    private static final YinYangDecider DEFAULT_DECIDER = () -> LocalDateTime.now().getHour() < 12;
    private final YinYangDecider decider;

    @FunctionalInterface
    public interface YinYangDecider {
        boolean isYin();
    }

    public YinYang(int yin, int yang) {
        this(yin, yang, DEFAULT_DECIDER);
    }
    public YinYang(int yin, int yang, YinYangDecider decider) {
        this(new Experience(yin), new Experience(yang), decider);
    }

    public int yinExp() {
        return yin.value();
    }

    public int yangExp() {
        return yang.value();
    }

    public YinYang(Experience yinExp, Experience yangExp, YinYangDecider decider) {
        this.yin = yinExp;
        this.yang = yangExp;
        this.decider = decider;
    }

    public int yinLevel() {
        return ExperienceUtil.computeLevel(yin.value() + 664);
    }
    public int yangLevel() {
        return ExperienceUtil.computeLevel(yang.value() + 664);
    }

    public boolean isYin() {
        return decider.isYin();
    }

    public YinYang() {
        this(0, 0);
    }

    public int age() {
        return ExperienceUtil.computeLevel(yin.value() + yang.value());
    }

    public boolean hasHigherLevel(YinYang another) {
        return decider.isYin() ? yinLevel() > another.yinLevel() :
                yangLevel() > another.yangLevel();
    }

    public YinYang accumulate(int seconds) {
        return decider.isYin() ? new YinYang(yin.gainExp(seconds), yang, decider)
                 : new YinYang(yin, yang.gainExp(seconds), decider);
    }
}
