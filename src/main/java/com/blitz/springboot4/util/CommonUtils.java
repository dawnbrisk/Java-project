package com.blitz.springboot4.util;

import com.blitz.springboot4.entity.WarehouseLocation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CommonUtils {

    // 静态方法返回UUID
    public static String generateUUID() {

        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        // Generate a UUID and remove the hyphens
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return dateTime+"_"+uuid;
    }


    public static boolean areAdjacent(WarehouseLocation loc1, WarehouseLocation loc2) {
        String[] parts1 = loc1.getLocationCode().split("-");
        String[] parts2 = loc2.getLocationCode().split("-");

        // Ensure the first part and last part are the same
        if (!parts1[0].equals(parts2[0]) || !parts1[3].equals(parts2[3])) {
            return false;
        }

        // Convert the two middle numbers to integers
        int firstMiddle1 = Integer.parseInt(parts1[1]);
        int secondMiddle1 = Integer.parseInt(parts1[2]);

        int firstMiddle2 = Integer.parseInt(parts2[1]);
        int secondMiddle2 = Integer.parseInt(parts2[2]);

        // Only check adjacency if the first middle numbers are the same
        if (firstMiddle1 == firstMiddle2) {
            return Math.abs(secondMiddle1 - secondMiddle2) == 1;
        }

        return false;
    }

}
