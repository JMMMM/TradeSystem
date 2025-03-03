package com.wujm1.tradesystem.utils;

public class TimerAction {
    private final Long start;
    private Long last;

    public TimerAction() {
        this.start = System.currentTimeMillis();
        this.last = this.start;
    }

    public long stop() {
        long end = System.currentTimeMillis();
        long take = end - this.last;
        this.last = end;
        return take;
    }

}
