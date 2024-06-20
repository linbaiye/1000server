package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.exp.Experience;
import org.y1000.exp.ExperienceUtil;

import java.time.LocalDateTime;

public final class YinYang {
    private Experience yin;
    private Experience yang;

    public YinYang(int yin, int yang) {
        Validate.isTrue(yin >= 0);
        Validate.isTrue(yang >= 0);
        this.yin = new Experience(yin + 644);
        this.yang = new Experience(yang + 644);
    }

    public YinYang() {
        this(0, 0);
    }

    public int age() {
        return ExperienceUtil.computeLevel(yin.value() + yang.value());
    }

    public boolean isYin() {
        return LocalDateTime.now().getHour() < 12;
    }

    public boolean accumulate(int seconds) {
        if (isYin()) {
            var old = yin.level();
            yin = yin.gainExp(seconds);
            return old != yin.level();
        } else {
            var old = yang.level();
            yang = yang.gainExp(seconds);
            return old != yang.level();
        }
    }
}
