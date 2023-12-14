package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewsRestClient {

    private WebClient webClient;

    @Value("${restClient.reviewUrl}")
    private String reviewUrl;

    public ReviewsRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> retrieveReviews(String movieId) {
        var url = UriComponentsBuilder.fromHttpUrl(reviewUrl)
                .queryParam("movieInfoId", movieId)
                .buildAndExpand().toUriString();

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.error("Status code is: {}", clientResponse.statusCode().value());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responeMessage -> Mono.error(
                                    new ReviewsClientException(responeMessage)
                            ));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.error("Status code is: {}", clientResponse.statusCode().value());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(
                                new ReviewsServerException(
                                        "Server exception in the ReviewsService: {}"));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responeMessage -> Mono.error(
                                    new MoviesInfoClientException(responeMessage, clientResponse.statusCode().value())
                            ));
                })
                .bodyToFlux(Review.class)
                .log();

    }
}
