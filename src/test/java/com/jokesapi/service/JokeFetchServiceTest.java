package com.jokesapi.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.jokesapi.exception.InvalidJokeResponseException;
import com.jokesapi.exception.JokeFetchException;
import com.jokesapi.model.JokeFetchDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings({ "unchecked", "rawtypes" })
class JokeFetchServiceTest {

	@Mock
	private WebClient.RequestHeadersUriSpec requestHeadersUriSpec; // No generics here

	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpec; // No generics here

	@Mock
	private WebClient.ResponseSpec responseSpec;

	@InjectMocks
	private JokeFetchService jokeFetchService;

	@Mock
	private WebClient webClient;

	@Mock
	private Logger mockLogger; // Mock the logger

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(jokeFetchService, "log", mockLogger);
	}

	@Test
	void fetchJokesTest() {

		WebClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri("https://official-joke-api.appspot.com/random_joke"))
				.thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(JokeFetchDto.class))
				.thenReturn(Mono.just(new JokeFetchDto("1", "Hi how are you", "i'm greate what about you")));

		Flux<JokeFetchDto> fetchAndSaveJokes = jokeFetchService.fetchJokes(1);

		List<JokeFetchDto> result = fetchAndSaveJokes.collectList().block();

		// Verify the result
		assertEquals(1, result.size());
		assertEquals("1", result.get(0).getId());
		assertEquals("Hi how are you", result.get(0).getQuestion());
		assertEquals("i'm greate what about you", result.get(0).getAnswer());
	}

	@Test
	void fetchJokesIsNull() {

	   
	    when(webClient.get()).thenReturn(requestHeadersUriSpec);
	    when(requestHeadersUriSpec.uri("https://official-joke-api.appspot.com/random_joke"))
	            .thenReturn(requestHeadersSpec);
	    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
	    when(responseSpec.bodyToMono(JokeFetchDto.class))
	            .thenReturn(Mono.just(new JokeFetchDto(null, null, null)));

	   
	    InvalidJokeResponseException thrown = null;

	   
	    try {
	        jokeFetchService.fetchJokes(1).blockFirst();
	    } catch (InvalidJokeResponseException e) {
	        thrown = e; // 
	    }

	   
	    assertNotNull("Expected InvalidJokeResponseException to be thrown", thrown);

	   
	    assertEquals("Invalid joke response: JokeFetchDto(id=null, question=null, answer=null)", thrown.getMessage());
	}

	@Test
	void fetchJokeException() {

	    when(webClient.get()).thenReturn(requestHeadersUriSpec);
	    when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
	    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
	    when(responseSpec.bodyToMono(JokeFetchDto.class)).thenReturn(Mono.error(new RuntimeException("Network error")));

	    // Variable to store the exception
	    JokeFetchException thrown = null;

	    
	    try {
	        jokeFetchService.fetchJokes(1).blockFirst(); 
	    } catch (JokeFetchException e) {
	        thrown = e; 
	    }

	    
	    assertNotNull("Expected JokeFetchException to be thrown", thrown);

	    
	    verify(mockLogger).error("Error fetching joke: {}", "Network error");

	    
	    assertEquals("Failed to fetch joke from server: Network error", thrown.getMessage());
	}

}
