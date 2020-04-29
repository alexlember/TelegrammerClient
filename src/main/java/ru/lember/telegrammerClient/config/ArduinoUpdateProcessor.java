package ru.lember.telegrammerClient.config;

import reactor.core.publisher.Flux;
import ru.lember.telegrammerClient.dto.inner.ArduinoDataUpdate;

import java.util.function.Predicate;

public interface ArduinoUpdateProcessor {

    void onNext(ArduinoDataUpdate update);
    Flux<ArduinoDataUpdate> filter(Predicate<ArduinoDataUpdate> filter);
}
