package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.*;
import com.reactivespring.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {

    @Autowired
    private Validator validator;

    private ReviewRepository reviewRepository;

    Sinks.Many<Review> reviewSink = Sinks.many().replay().all();

    public ReviewHandler(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /*public Mono<ServerResponse> addReview(ServerRequest request) {
     *//*return request.bodyToMono(Review.class)
                .flatMap(movieReview -> {
                    return reviewRepository.save(movieReview);
                })
                .flatMap(savedReview -> {
                    return ServerResponse.status(HttpStatus.CREATED).bodyValue(savedReview);
                });*//*

        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);

        *//*var savedReview = request.bodyToMono(Review.class)
                .flatMap(data -> reviewRepository.save(data));
        return ServerResponse.status(HttpStatus.CREATED).body(savedReview, Review.class);*//*
    }*/

    /**
     * WITH SINK IMPLEMENTATION
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> addReview(ServerRequest request) {
        /*return request.bodyToMono(Review.class)
                .flatMap(movieReview -> {
                    return reviewRepository.save(movieReview);
                })
                .flatMap(savedReview -> {
                    return ServerResponse.status(HttpStatus.CREATED).bodyValue(savedReview);
                });*/

        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewRepository::save)
                .doOnNext(review -> {
                    reviewSink.tryEmitNext(review);
                })
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);

        /*var savedReview = request.bodyToMono(Review.class)
                .flatMap(data -> reviewRepository.save(data));
        return ServerResponse.status(HttpStatus.CREATED).body(savedReview, Review.class);*/
    }

    private void validate(Review review) {
        var constraintViolation = validator.validate(review);
        log.info("Contraintvaiolation: {}", constraintViolation);
        if (!constraintViolation.isEmpty()) {
            var errorMessage = constraintViolation
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMessage);
        }
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        var movieInfoId = request.queryParam("movieInfoId");

        Flux<Review> reviewFlux;
        if (movieInfoId.isPresent()) {
            reviewFlux = reviewRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
        } else {
            reviewFlux = reviewRepository.findAll();
        }
        return buildReviewsResponse(reviewFlux);

    }

    private static Mono<ServerResponse> buildReviewsResponse(Flux<Review> reviewFlux) {
        return ServerResponse.ok().body(reviewFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");
        /*var existingReview = reviewRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given review id " + reviewId)));*/
        var existingReview = reviewRepository.findById(reviewId);
        //.switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given review id " + reviewId)));

        /*return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            return review;
                        })
                        .flatMap(reviewRepository::save))
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview));*/
        return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            return review;
                        })
                        .flatMap(reviewRepository::save))
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");
        var existingReview = reviewRepository.findById(reviewId);

        return existingReview
                .flatMap(review -> reviewRepository.deleteById(reviewId))
                .then(ServerResponse.noContent().build());

    }

    public Mono<ServerResponse> getReviewsStream(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewSink.asFlux(), Review.class)
                .log();
    }
}
