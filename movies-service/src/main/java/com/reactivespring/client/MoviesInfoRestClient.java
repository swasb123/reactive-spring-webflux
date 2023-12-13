package com.reactivespring.client;

import com.reactivespring.config.WebClientConfig;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MoviesInfoRestClient {

    private WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieInfoId) {
        var url = moviesInfoUrl.concat("/{id}");

        return webClient
                .get()
                .uri(url, movieInfoId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.error("Status code is: {}", clientResponse.statusCode().value());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(
                                new MoviesInfoClientException(
                                        "There is no movie info available for the passed id: " + movieInfoId,
                                        clientResponse.statusCode().value()));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responeMessage -> Mono.error(
                                    new MoviesInfoClientException(responeMessage, clientResponse.statusCode().value())
                            ));
                })
                .bodyToMono(MovieInfo.class)
                .log();

    }

}
