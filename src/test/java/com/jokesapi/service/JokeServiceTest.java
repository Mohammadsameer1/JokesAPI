package com.jokesapi.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import com.jokesapi.model.Joke;
import com.jokesapi.repository.JokeRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JokeServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private JokeRepository jokeRepository;

    @InjectMocks
    private JokeService jokeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void fetchAndSaveJokesTest() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("https://api.example.com/jokes")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Joke.class)).thenReturn(Mono.just(new Joke("1", "What do you call a bear with no teeth?", "A gummy bear")));

        Flux<Joke> fetchAndSaveJokes = jokeService.fetchAndSaveJokes(1);

        List<Joke> block = fetchAndSaveJokes.collectList().block();

        assertEquals(1, block.size());
        assertEquals("1", block.get(0).getId());
    }
}
