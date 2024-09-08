
package com.jokesapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jokesapi.exception.JokeFetchException;
import com.jokesapi.model.JokeApiResponse;
import com.jokesapi.model.JokeFetchDto;
import com.jokesapi.model.JokeSaveDto;
import com.jokesapi.service.JokeFetchService;
import com.jokesapi.service.JokeInsertService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class JokeControllerTest {

	@Mock
	private JokeFetchService jokeFetchService;

	@Mock
	private JokeInsertService jokeInsertService;

	@InjectMocks
	private JokeController jokeController;

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getJokesSuccesTest() {

		int validCount = 2;
		JokeFetchDto jokeFetchDto = new JokeFetchDto("1", "Hi how are you", "I'm fine, how about you");

		when(jokeFetchService.fetchJokes(validCount)).thenReturn(Flux.just(jokeFetchDto));
		when(jokeInsertService.insertIfNotExists(any(JokeSaveDto.class)))
				.thenReturn(Mono.just(new JokeSaveDto("1", jokeFetchDto.getQuestion(), jokeFetchDto.getAnswer())));

		Mono<ResponseEntity<JokeApiResponse>> responseMono = jokeController.getJokes(validCount);

		ResponseEntity<JokeApiResponse> response = responseMono.block();

		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().getJokes().size());
		assertEquals("1", response.getBody().getJokes().get(0).getId());
	}

	@Test
	void getJokesFetchFailuresTest() {

		int validCount = 1;

		when(jokeFetchService.fetchJokes(validCount))
				.thenReturn(Flux.error(new JokeFetchException("Failed to fetch joke from server")));

		Mono<ResponseEntity<JokeApiResponse>> responseMono = jokeController.getJokes(validCount);

		ResponseEntity<JokeApiResponse> response = responseMono.block();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void getJokesInsertFailureTest() {

		int validCount = 1;
		JokeFetchDto jokeFetchDto = new JokeFetchDto("1", "How are you", "Very bad");

		when(jokeFetchService.fetchJokes(validCount)).thenReturn(Flux.just(jokeFetchDto));
		when(jokeInsertService.insertIfNotExists(any(JokeSaveDto.class)))
				.thenReturn(Mono.error(new RuntimeException("Insert failed")));

		Mono<ResponseEntity<JokeApiResponse>> responseMono = jokeController.getJokes(validCount);

		ResponseEntity<JokeApiResponse> response = responseMono.block();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void getJokesBadRequestTest() {

		int invalidCount = 0;

		Mono<ResponseEntity<JokeApiResponse>> responseMono = jokeController.getJokes(invalidCount);

		ResponseEntity<JokeApiResponse> response = responseMono.block();

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}
