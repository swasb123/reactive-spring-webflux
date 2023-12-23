package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinksTest {

    @Test
    void sinks() {
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux
                .log()
                .subscribe((i) -> {
                    System.out.println("Subscriber 1: " + i);
                });

        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux1
                .log()
                .subscribe((i) -> {
                    System.out.println("Subscriber 2: " + i);
                });

        replaySink.tryEmitNext(3);
    }

    @Test
    void sinks_multicast() {
        Sinks.Many<Integer> multicast = Sinks.many().multicast().onBackpressureBuffer();
        multicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = multicast.asFlux();
        integerFlux
                //.log()
                .subscribe((i) -> {
                    System.out.println("Subscriber 1: " + i);
                });

        Flux<Integer> integerFlux1 = multicast.asFlux();
        integerFlux1
                //.log()
                .subscribe((i) -> {
                    System.out.println("Subscriber 1: " + i);
                });

        multicast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Test
    void sinks_unicast() {
        Sinks.Many<Integer> unicast = Sinks.many().unicast().onBackpressureBuffer();
        unicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        unicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = unicast.asFlux();
        integerFlux
                //.log()
                .subscribe((i) -> {
                    System.out.println("Subscriber 1: " + i);
                });

        Flux<Integer> integerFlux1 = unicast.asFlux();
        integerFlux1
                //.log()
                .subscribe((i) -> {
                    System.out.println("Subscriber 1: " + i);
                });

        unicast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
