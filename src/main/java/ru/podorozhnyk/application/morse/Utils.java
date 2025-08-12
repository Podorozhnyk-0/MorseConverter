package ru.podorozhnyk.application.morse;

import java.util.ArrayList;
import java.util.List;

public final class Utils {
    public static String requireNonBlank(String str, String errorMsg) {
        if (str == null || str.isBlank()) throw new IllegalArgumentException(errorMsg);
        return str;
    }

    public static String requireNonEmpty(String str, String errorMsg) {
        if (str == null || str.isEmpty()) throw new IllegalArgumentException(errorMsg);
        return str;
    }

    public static  <T> List<T> swapElements(List<T> list, int i1, int i2) {
        if (i1 == i2) throw new IllegalArgumentException("i1 cannot be equal i2.");
        ArrayList<T> copyList = new ArrayList<>(list);
        T value1 = copyList.get(i1);
        T value2 = copyList.get(i2);
        if (i1 > i2) {
            copyList.remove(i1);
            copyList.remove(i2);

            copyList.add(i2, value1);
            copyList.add(i1, value2);
        }
        else {
            copyList.remove(i2);
            copyList.remove(i1);

            copyList.add(i1, value2);
            copyList.add(i2, value1);
        }
        return copyList;
    }
}
