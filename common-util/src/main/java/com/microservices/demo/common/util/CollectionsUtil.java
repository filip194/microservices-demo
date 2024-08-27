package com.microservices.demo.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Thread safe singleton
 */
public class CollectionsUtil {

    private CollectionsUtil() {
    }

    public static CollectionsUtil getInstance() {
        return CollectionsUtilHolder.INSTANCE;
    }

    public <T> List<T> getListFromIterable(Iterable<T> iterable) {
        final List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;

    }

    private static class CollectionsUtilHolder {
        static final CollectionsUtil INSTANCE = new CollectionsUtil();
    }
}

