package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .log();
    }

    public Flux<String> namesFlux_map() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .log();
    }

    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Flux<String> namesFlux_filter(int stringLength) {
        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(str -> str.length() > stringLength)
                .map(str -> str.length() + "-" + str)
                .log();
    }

    public Flux<String> namesFlux_flatmap(int stringLength) {
        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(str -> str.length() > stringLength)
                // ALEX, CHLOE -> A,L,E,X,C,H,L,O,E
                .flatMap(s -> splitString(s))
                .log();
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(str -> str.length() > stringLength)
                // ALEX, CHLOE -> A,L,E,X,C,H,L,O,E
                .flatMap(s -> splitString_withDelay(s))
                .log();
    }

    public Flux<String> namesFlux_concatmap_async(int stringLength) {
        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(str -> str.length() > stringLength)
                // ALEX, CHLOE -> A,L,E,X,C,H,L,O,E
                .concatMap(s -> splitString_withDelay(s))
                .log();
    }

    public Flux<String> namesFlux_transform(int stringLength) {
        // filter the string whose length is greater than 3

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(str -> str.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(s -> splitString(s)) // ALEX, CHLOE -> A,L,E,X,C,H,L,O,E
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {
        // filter the string whose length is greater than 3

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(str -> str.length() > stringLength)
                .flatMap(s -> splitString(s));

        var defaultFlux = Flux.just("default")
                .transform(filterMap); // D,E,F,A,U,L,T

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> splitString_withDelay(String name) {
        var charArray = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

    public Mono<String> nameMono() {
        return Mono.just("alex")
                .log();
    }

    public Mono<String> nameMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .log();
    }

    public Mono<List<String>> nameMono_flatmap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> nameMono_flatmapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    private <R> Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);

    }

    public Flux<String> explore_concat() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux)
                .log();

    }

    public Flux<String> explore_concatwith() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux)
                .log();

    }

    public Flux<String> explore_concatwith_mono() {

        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.concatWith(bMono)
                .log();

    }

    public Flux<String> explore_merge() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux)
                .log();

    }

    public Flux<String> explore_merge_with() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux)
                .log();

    }

    public Flux<String> explore_merge_with_mono() {

        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.mergeWith(bMono)
                .log();

    }

    public Flux<String> explore_mergesequencial() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux)
                .log();

    }

    public Flux<String> explore_zip() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second)
                .log(); //AD, BE, CF

    }

    public Flux<String> explore_zip_1() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        var _123Flux = Flux.just("1", "2", "3");

        var _456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log(); // "AD14", "BE25", "CF36"

    }

    public Flux<String> explore_zipwith() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        //return Flux.zip(abcFlux, defFlux, (first, second) -> first + second)
        return abcFlux.zipWith(defFlux, (first, second) -> first + second)
                .log(); //AD, BE, CF

    }

    public Mono<String> explore_zipwith_mono() {

        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log(); //AB

    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

//        fluxAndMonoGeneratorService.namesFlux()
//                .subscribe(name -> System.out.println("Flux Name is: " + name));
//        System.out.println("------------------");
//        fluxAndMonoGeneratorService.nameMono()
//                .subscribe(name -> System.out.println("Mono Name is: " + name));
//        System.out.println("------------------");
//        fluxAndMonoGeneratorService.namesFlux_map()
//                .subscribe(System.out::println);
//        System.out.println("------------------");
//        fluxAndMonoGeneratorService.namesFlux_immutability()
//                .subscribe(name -> System.out.println("Flux immutability: " + name));
//
//        System.out.println("------------------");
        fluxAndMonoGeneratorService.namesFlux_flatmap(3)
                .subscribe(name -> System.out.println("Flatmap filter: " + name));
    }
}
