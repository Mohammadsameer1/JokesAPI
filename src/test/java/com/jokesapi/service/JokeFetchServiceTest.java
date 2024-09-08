package com.jokesapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

import com.jokesapi.model.JokeFetchDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@SuppressWarnings({ "unchecked", "rawtypes" })
class JokeFetchServiceTest {

	@InjectMocks
	private JokeFetchService jokeFetchService;

	@Mock
	private WebClient webClient;

	@Mock
	private RequestHeadersUriSpec<?> requestHeadersUriSpec;

	@Mock
	private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

	@Mock
	private WebClient.ResponseSpec responseSpec;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	
	@Test
	void fetchJokesTest1() {
		
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

}
