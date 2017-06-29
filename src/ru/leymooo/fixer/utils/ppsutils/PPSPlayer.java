package ru.leymooo.fixer.utils.ppsutils;

import ru.leymooo.fixer.Main;

public class PPSPlayer {
    
    private long startTime = System.currentTimeMillis();
    private int pps = -1;
    
    private void incrementReceived() {
        Long diff = System.currentTimeMillis() - startTime;
        if (diff >= 1000) {
            this.pps = 0;
            this.startTime = System.currentTimeMillis();
        }
        this.pps++;
    }
    
    public boolean handlePPS() {
        this.incrementReceived();
        if (Main.maxPPS > 0) {
            if (this.pps > Main.maxPPS) {
                return true;
            }
        }
        return false;
    }
}
