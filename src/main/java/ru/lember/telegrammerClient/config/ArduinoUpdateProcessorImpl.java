package ru.lember.telegrammerClient.config;

import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import ru.lember.telegrammerClient.dto.inner.ArduinoDataUpdate;

import java.util.function.Predicate;

public class ArduinoUpdateProcessorImpl implements ArduinoUpdateProcessor {

    private ReplayProcessor<ArduinoDataUpdate> processor = ReplayProcessor.create(1);

    @Override
    public void onNext(ArduinoDataUpdate update) {
        processor.onNext(update);
    }

    @Override
    public Flux<ArduinoDataUpdate> filter(Predicate<ArduinoDataUpdate> filter) {
        return processor.filter(filter);
    }

}
