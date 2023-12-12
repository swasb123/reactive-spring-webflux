package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewRepository reviewRepository;

    static String REVIEW_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {
        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review("abc", 2L, "Excellent Movie", 8.0));
        reviewRepository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll().block();
    }

    @Test
    void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

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
    void addReview_validation() {
        var review = new Review(null, null, "Awesome Movie", -9.0);

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void getReviews() {
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
    void getReviewsByMovieInfoId() {
        var uri = UriComponentsBuilder.fromUriString(REVIEW_URL)
                .queryParam("movieInfoId", "1")
                .buildAndExpand().toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);

    }

    @Test
    void updateReview() {
        var updateReviewData = new Review(null, 1L, "Awesome Movie1", 9.0);
        var reviewId = "abc";

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
                    assertEquals("Awesome Movie1", updatedReviewInfo.getComment());

                });
    }

    @Test
    void deleteReviewId() {
        var reviewId = "abc";

        webTestClient
                .delete()
                .uri(REVIEW_URL + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();
//                .is2xxSuccessful()
//                .expectBody(Void.class);
    }
}
