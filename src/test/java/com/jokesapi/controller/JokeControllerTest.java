package com.jokesapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jokesapi.model.Joke;
import com.jokesapi.service.JokeService;

import reactor.core.publisher.Flux;

class JokeControllerTest {

	@Mock
	private JokeService jokeService;

	@InjectMocks
	private JokeController jokeController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
    void getJokesTest() {

        when(jokeService.fetchAndSaveJokes(1)).thenReturn(Flux.just(new Joke("1", "question", "answer")));


        ResponseEntity<Flux<Joke>> response = jokeController.getJokes(1);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().count().block());
    }

	@Test
	void getJokesNegativeTestBelowMin() {

		ResponseEntity<Flux<Joke>> response = jokeController.getJokes(0);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void getJokesNegativeTestAboveMax() {

		ResponseEntity<Flux<Joke>> response = jokeController.getJokes(101);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}
