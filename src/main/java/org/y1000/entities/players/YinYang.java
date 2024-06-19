package org.y1000.entities.players;

import org.y1000.exp.Experience;

import java.time.LocalDateTime;

public final class YinYang {
    private static class SecondsLevel {
        private int seconds;
        private int level;

        public SecondsLevel(int seconds) {
            this.seconds = seconds;
            this.level = Experience.computeLevel(seconds + 644);
        }

        public boolean accumulate(int seconds) {
            this.seconds += seconds;
            int n = Experience.computeLevel(this.seconds + 664);
            if (n != level) {
                level = n;
                return true;
            }
            return false;
        }
    }

    private final SecondsLevel yin;
    private final SecondsLevel yang;

    public YinYang(int yin, int yang) {
        this.yin = new SecondsLevel(yin);
        this.yang = new SecondsLevel(yang);
    }

    public int age() {
        return Experience.computeLevel(yin.seconds + yang.seconds);
    }

    public boolean accumulate(int seconds) {
        int hour = LocalDateTime.now().getHour();
        return hour < 12 ? yin.accumulate(seconds) : yang.accumulate(seconds);
    }
}
