package com.blitz.springboot4.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static AtomicInteger sequence = new AtomicInteger(0);
    private static final int MAX_SEQUENCE = 9999;

    public static synchronized String generateId() {
        String timestamp = LocalDateTime.now().format(formatter);
        int seq = sequence.getAndIncrement();
        if (seq > MAX_SEQUENCE) {
            sequence.set(0);
            seq = sequence.getAndIncrement();
        }
        // 递增序号，左补0保证4位宽度
        String seqStr = String.format("%04d", seq);
        return timestamp + seqStr;
    }
}
