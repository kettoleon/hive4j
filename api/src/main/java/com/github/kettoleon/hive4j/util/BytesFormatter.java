package com.github.kettoleon.hive4j.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BytesFormatter {

    public static String toHumanReadableSuffix(long bytes) {
        return toHumanReadableWithUnitsAndPrefixes(bytes, BytesFormatter::defaultFormatter, "", "k", "M", "G", "T");
    }

    public static String toHumanReadableBytes(long bytes) {
        return toHumanReadableWithUnitsAndPrefixes(bytes, BytesFormatter::defaultFormatter, " bytes", " KiB", " MiB", " GiB", " TiB");
    }

    public static String toHumanReadableDownloadBytes(long bytes) {
        return toHumanReadableWithUnitsAndPrefixes(bytes, BytesFormatter::downloadFormatter, " bytes", " KiB", " MiB", " GiB", " TiB");
    }

    private static String toHumanReadableWithUnitsAndPrefixes(long bytes, ValueFormatter formatter, String... unit) {

        for (int i = 0; i < unit.length; i++) {
            if (bytes < pow(i + 1)) {
                return formatter.format(bytes / pow(i), i) + unit[i];
            }
        }

        return formatter.format(bytes / pow(unit.length - 1), unit.length - 1) + unit[unit.length - 1];
    }

    private static double pow(int power) {
        return Math.pow(1024, power);
    }

    private static String downloadFormatter(double value, int pow) {
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.FLOOR);
        if (pow <= 2) {
            return df.format(value);
        }
        df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.FLOOR);
        return df.format(value);
    }

    private static String defaultFormatter(double v, int power) {
        int withDecimals = (int) Math.floor(v * 100.0);

        if (withDecimals % 100 == 0) {
            return (withDecimals / 100) + "";
        }
        return (withDecimals / 100.0) + "";
    }

    private interface ValueFormatter {
        String format(double value, int power);
    }

}
