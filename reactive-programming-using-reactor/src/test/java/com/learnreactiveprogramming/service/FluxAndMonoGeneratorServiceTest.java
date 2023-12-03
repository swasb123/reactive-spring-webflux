package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void testNamesFlux() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
                //.expectNext("alex", "ben", "chloe")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFlux_map() {
        var namesFluxMap = fluxAndMonoGeneratorService.namesFlux_map();

        StepVerifier.create(namesFluxMap)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        var namesFluxMap = fluxAndMonoGeneratorService.namesFlux_immutability();

        StepVerifier.create(namesFluxMap)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();

    }

    @Test
    void namesFlux_filter() {
        int stringLength = 3;
        var namesFluxFilter = fluxAndMonoGeneratorService.namesFlux_filter(stringLength);

        StepVerifier.create(namesFluxFilter)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {
        int stringLength = 3;
        var namesFluxFlatmap = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);

        StepVerifier.create(namesFluxFlatmap)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();

    }

    @Test
    void splitString_withDelay() {
        int stringLength = 3;
        var namesFluxFlatmapAsync = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);

        StepVerifier.create(namesFluxFlatmapAsync)
                //.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap_async() {
        int stringLength = 3;
        var namesFluxFlatmapAsync = fluxAndMonoGeneratorService.namesFlux_concatmap_async(stringLength);

        StepVerifier.create(namesFluxFlatmapAsync)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void nameMono_flatmap() {
        int stringLength = 3;
        var nameMonoFlatmap = fluxAndMonoGeneratorService.nameMono_flatmap(stringLength);

        StepVerifier.create(nameMonoFlatmap)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();


    }

    @Test
    void nameMono_flatmapMany() {
        int stringLength = 3;
        var nameMonoFlatmap = fluxAndMonoGeneratorService.nameMono_flatmapMany(stringLength);

        StepVerifier.create(nameMonoFlatmap)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        int stringLength = 3;
        var namesFluxFlatmap = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(namesFluxFlatmap)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_empty() {
        int stringLength = 6;
        var namesFluxFlatmap = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(namesFluxFlatmap)
                //.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {
        int stringLength = 6;
        var namesFluxFlatmap = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength);

        StepVerifier.create(namesFluxFlatmap)
                //.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        var concatFlux = fluxAndMonoGeneratorService.explore_concat();

        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatwith() {
        var concatwithFlux = fluxAndMonoGeneratorService.explore_concatwith();

        StepVerifier.create(concatwithFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatwith_mono() {
        var concatwithMono = fluxAndMonoGeneratorService.explore_concatwith_mono();

        StepVerifier.create(concatwithMono)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_merge() {
        var mergeFlux = fluxAndMonoGeneratorService.explore_merge();

        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void explore_mergewith() {
        var mergeFlux = fluxAndMonoGeneratorService.explore_merge_with();

        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void explore_merge_with_mono() {
        var mergewithMono = fluxAndMonoGeneratorService.explore_merge_with_mono();

        StepVerifier.create(mergewithMono)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_mergesequencial() {
        var mergeSequencial = fluxAndMonoGeneratorService.explore_mergesequencial();

        StepVerifier.create(mergeSequencial)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_zip() {
        var fluxZip = fluxAndMonoGeneratorService.explore_zip();

        StepVerifier.create(fluxZip)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zip_1() {
        var fluxZip = fluxAndMonoGeneratorService.explore_zip_1();

        StepVerifier.create(fluxZip)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void explore_zipwith() {
        var fluxZipwith = fluxAndMonoGeneratorService.explore_zipwith();

        StepVerifier.create(fluxZipwith)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zipwith_mono() {
        var zipwithMono = fluxAndMonoGeneratorService.explore_zipwith_mono();

        StepVerifier.create(zipwithMono)
                .expectNext("AB")
                .verifyComplete();
    }
}