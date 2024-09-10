
package com.jokesapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jokesapi.model.JokeApiResponse;
import com.jokesapi.model.JokeResponseDto;
import com.jokesapi.service.JokeService;

import reactor.core.publisher.Mono;

class JokeControllerTest {

	@Mock
	private JokeService jokeService; // mocking jokeservice

	@InjectMocks
	private JokeController jokeController; // injecting the mock into jokecontroller

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getJokesSuccesTest() {

		int validCount = 1;

		JokeResponseDto jokeResponseDto = new JokeResponseDto("1", "hi how are you", "im fine");

		List<JokeResponseDto> jokeResponseList = new ArrayList<>();

		jokeResponseList.add(jokeResponseDto);

		JokeApiResponse mockedResponse = new JokeApiResponse(jokeResponseList);

		when(jokeService.fetchAndSaveJokes(anyInt())).thenReturn(Mono.just(mockedResponse));

		Mono<ResponseEntity<JokeApiResponse>> responseMono = jokeController.getJokes(validCount);

		ResponseEntity<JokeApiResponse> responseEntity = responseMono.block();

		assertNotNull(responseEntity);
		assertNotNull(responseEntity.getBody());

		JokeResponseDto joke = responseEntity.getBody().getJokes().get(0);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("1", joke.getId());

		assertEquals("hi how are you", joke.getQuestion());
		assertEquals("im fine", joke.getAnswer());

	}

	// invalid Max count test

	@Test
	void getJokesInvalidCounMaxtTest() {

		int inValidMaxCount = 200;

		Mono<ResponseEntity<JokeApiResponse>> responseMono = jokeController.getJokes(inValidMaxCount);

		ResponseEntity<JokeApiResponse> block = responseMono.block();

		assertEquals(HttpStatus.BAD_REQUEST, block.getStatusCode());

	}

	// invalid min count test

	@Test
	void getJokesInvalidCountMinTest() {

		int invalidMinCount = -1;

		Mono<ResponseEntity<JokeApiResponse>> jokes = jokeController.getJokes(invalidMinCount);

		ResponseEntity<JokeApiResponse> block = jokes.block();

		assertEquals(HttpStatus.BAD_REQUEST, block.getStatusCode());

	}

	@Test
	void getJokeExceptionTest() {

		int validCount = 1;

		when(jokeService.fetchAndSaveJokes(anyInt())).thenReturn(Mono.error(new RuntimeException("service failure")));

		Mono<ResponseEntity<JokeApiResponse>> jokes = jokeController.getJokes(validCount);

		ResponseEntity<JokeApiResponse> block = jokes.block();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, block.getStatusCode());

	}

}
