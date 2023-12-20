package com.github.kettoleon.hive4j.util;

import org.junit.jupiter.api.Test;

import static com.github.kettoleon.hive4j.util.BytesFormatter.toHumanReadableBytes;
import static com.github.kettoleon.hive4j.util.BytesFormatter.toHumanReadableSuffix;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class BytesFormatterTest {


    @Test
    void toHumanReadableBytes_returnsExpectedString() {
        assertThat(toHumanReadableBytes(1), equalTo("1 bytes"));
        assertThat(toHumanReadableBytes(1023), equalTo("1023 bytes"));
        assertThat(toHumanReadableBytes(1024), equalTo("1 KiB"));
        assertThat(toHumanReadableBytes(1025), equalTo("1 KiB"));
        assertThat(toHumanReadableBytes(1034), equalTo("1 KiB"));
        assertThat(toHumanReadableBytes(1035), equalTo("1.01 KiB"));
        assertThat(toHumanReadableBytes(getPow(1) + 512), equalTo("1.5 KiB"));
        assertThat(toHumanReadableBytes(2 * getPow(1)), equalTo("2 KiB"));
        assertThat(toHumanReadableBytes(getPow(2)), equalTo("1 MiB"));
        assertThat(toHumanReadableBytes(getPow(3)), equalTo("1 GiB"));
        assertThat(toHumanReadableBytes(getPow(4)), equalTo("1 TiB"));
        assertThat(toHumanReadableBytes(getPow(5)), equalTo("1024 TiB"));

    }

    @Test
    void toHumanReadableSuffix_returnsExpectedString() {
        assertThat(toHumanReadableSuffix(1), equalTo("1"));
        assertThat(toHumanReadableSuffix(1023), equalTo("1023"));
        assertThat(toHumanReadableSuffix(1024), equalTo("1k"));
        assertThat(toHumanReadableSuffix(1025), equalTo("1k"));
        assertThat(toHumanReadableSuffix(1034), equalTo("1k"));
        assertThat(toHumanReadableSuffix(1035), equalTo("1.01k"));
        assertThat(toHumanReadableSuffix(getPow(1) + 512), equalTo("1.5k"));
        assertThat(toHumanReadableSuffix(2 * getPow(1)), equalTo("2k"));
        assertThat(toHumanReadableSuffix(getPow(2)), equalTo("1M"));
        assertThat(toHumanReadableSuffix(getPow(3)), equalTo("1G"));
        assertThat(toHumanReadableSuffix(getPow(4)), equalTo("1T"));
        assertThat(toHumanReadableSuffix(getPow(5)), equalTo("1024T"));

    }


    private static long getPow(int power) {
        return (long) Math.pow(1024, power);
    }
}