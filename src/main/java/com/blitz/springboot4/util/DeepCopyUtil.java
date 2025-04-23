package com.blitz.springboot4.util;

import java.util.ArrayList;
import java.util.List;

public class DeepCopyUtil {
    public static <T extends Cloneable> List<T> deepCopyByClone(List<T> src) {
        List<T> dest = new ArrayList<>(src.size());
        for (T item : src) {
            // 假设元素类正确实现了clone()方法
            dest.add(cloneItem(item));
        }
        return dest;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Cloneable> T cloneItem(T item) {
        try {
            // 通过反射调用clone方法确保访问权限
            return (T) item.getClass().getMethod("clone").invoke(item);
        } catch (Exception e) {
            throw new RuntimeException("Clone failed", e);
        }
    }
}