package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewRepository;
import com.reactivespring.router.ReviewRouter;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    ReviewRepository reviewRepository;

    @Autowired
    WebTestClient webTestClient;

    static String REVIEW_URL = "/v1/reviews";

    @Test
    void addReview() {

        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        when(reviewRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();

                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;

                });
    }

    @Test
    void addReview_validate() {

        var review = new Review(null, null, "Awesome Movie", -9.0);

        when(reviewRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
        .expectBody(String.class)
        .isEqualTo("rating.movieInfoId can not be null,rating.negative : please pass a non-negative value");
    }

    @Test
    void getAllReviews() {
        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review("abc", 2L, "Excellent Movie", 8.0));

        when(reviewRepository.findAll()).thenReturn(Flux.fromIterable(reviewsList));

        webTestClient
                .get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateReview() {

        var updateReviewData = new Review(null, 1L, "Awesome Movie1", 9.0);
        var reviewId = "abc";

        when(reviewRepository.save(isA(Review.class))).thenReturn(Mono.just(new Review("abc", 1L, "Not an Awesome Movie", 8.0)));
        when(reviewRepository.findById((String) any())).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .put()
                .uri(REVIEW_URL + "/{id}", reviewId)
                .bodyValue(updateReviewData)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(result -> {
                    var updatedReviewInfo = result.getResponseBody();

                    assert updatedReviewInfo != null;
                    assert updatedReviewInfo.getReviewId() != null;
                    assertEquals("Not an Awesome Movie", updatedReviewInfo.getComment());

                });
    }

    @Test
    void deleteReview() {

        var reviewId= "abc";
        when(reviewRepository.findById((String) any())).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
        when(reviewRepository.deleteById((String) any())).thenReturn(Mono.empty());

        //when
        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", reviewId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
