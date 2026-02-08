package com.oran.oranaiagent.utils;

import java.util.*;

public class BatchUtils {
    public static <T> List<List<T>> partition(List<T> list, int batchSize) {
        if (list == null || list.isEmpty() || batchSize <= 0) {
            return Collections.emptyList();
        }
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }
}
