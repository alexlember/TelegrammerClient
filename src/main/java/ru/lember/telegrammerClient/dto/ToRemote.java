package ru.lember.telegrammerClient.dto;

import org.jetbrains.annotations.Nullable;

public interface ToRemote {

    String cmd();
    String message();

    default @Nullable Long correlationId() {
        return null;
    }
}
