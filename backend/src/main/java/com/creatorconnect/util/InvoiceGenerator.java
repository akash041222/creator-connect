package com.creatorconnect.util;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

/** Generates human-readable, sequential-looking invoice numbers, e.g. CC-2026-000123. */
public final class InvoiceGenerator {

    private static final AtomicLong COUNTER = new AtomicLong(1000);

    private InvoiceGenerator() {}

    public static String next() {
        long seq = COUNTER.incrementAndGet();
        return "CC-" + Year.now().getValue() + "-" + String.format("%06d", seq);
    }
}
