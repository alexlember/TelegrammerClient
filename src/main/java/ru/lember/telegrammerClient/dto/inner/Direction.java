package ru.lember.telegrammerClient.dto.inner;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum Direction {

    REQUEST("req"),
    RESPONSE("rsp");

    @Getter
    private String value;

    static Direction fromValue(String value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}
