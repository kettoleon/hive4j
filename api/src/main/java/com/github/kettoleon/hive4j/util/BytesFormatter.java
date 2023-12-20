package com.github.kettoleon.hive4j.util;

public class BytesFormatter {

    public static String toHumanReadableSuffix(long bytes) {
        return toHumanReadableWithUnitsAndPrefixes(bytes, "", "k", "M", "G", "T");
    }

    public static String toHumanReadableBytes(long bytes) {
        return toHumanReadableWithUnitsAndPrefixes(bytes, " bytes", " KiB", " MiB", " GiB", " TiB");
    }

    private static String toHumanReadableWithUnitsAndPrefixes(long bytes, String... unit) {

        for (int i = 0; i < unit.length; i++) {
            if (bytes < pow(i + 1)) {
                return format(bytes / pow(i)) + unit[i];
            }
        }

        return format(bytes / pow(unit.length - 1)) + unit[unit.length - 1];
    }

    private static double pow(int power) {
        return Math.pow(1024, power);
    }

    private static String format(double v) {
        int withDecimals = (int) Math.floor(v * 100.0);

        if (withDecimals % 100 == 0) {
            return (withDecimals / 100) + "";
        }
        return (withDecimals / 100.0) + "";
    }


}
