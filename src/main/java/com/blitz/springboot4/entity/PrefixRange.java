package com.blitz.springboot4.entity;

public enum PrefixRange {
    B_05("B-05", 11, 55),
    B_06("B-06", 11, 55),
    B_11("B-11", 11, 55),
    B_12("B-12", 11, 55),
    B_17("B-17", 11, 55),
    B_18("B-18", 11, 55),
    B_23("B-23", 11, 55),
    B_24("B-24", 11, 55),
    B_29("B-29", 11, 55),
    B_30("B-30", 11, 55);

    private final String prefix;
    private final int startRange;
    private final int endRange;

    PrefixRange(String prefix, int startRange, int endRange) {
        this.prefix = prefix;
        this.startRange = startRange;
        this.endRange = endRange;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getStartRange() {
        return startRange;
    }

    public int getEndRange() {
        return endRange;
    }

    // 判断库位字符串是否符合范围
    public static boolean isInRange(String location) {
        for (PrefixRange prefixRange : PrefixRange.values()) {
            if (location.startsWith(prefixRange.getPrefix())) {
                // 提取库位中的数字部分（例如，B-03-06-1 中的 03 和 06）
                String[] parts = location.split("-");

                if (parts.length >= 3) {
                    try {
                        // 获取第二部分数字，并检查是否在范围内
                        int secondPart = Integer.parseInt(parts[2]);
                        if (secondPart >= prefixRange.getStartRange() && secondPart <= prefixRange.getEndRange()) {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
