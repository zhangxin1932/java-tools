package com.zy.commons.did.utils;

import java.util.concurrent.atomic.AtomicLong;

public class PaddingAtomicLong extends AtomicLong {
    private static final long serialVersionUID = 1450018231677463174L;
    private volatile long p1;
    private volatile long p2;
    private volatile long p3;
    private volatile long p4;
    private volatile long p5;
    private volatile long p6 = 7L;

    public PaddingAtomicLong() {
    }

    public PaddingAtomicLong(long initialValue) {
        super(initialValue);
    }

    public long sumPadding2PreventOptimization() {
        return this.p1 + this.p2 + this.p3 + this.p4 + this.p5 + this.p6;
    }
}
