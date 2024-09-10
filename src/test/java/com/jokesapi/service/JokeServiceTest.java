package com.jokesapi.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jokesapi.model.JokeApiResponse;
import com.jokesapi.model.JokeFetchDto;
import com.jokesapi.model.JokeResponseDto;
import com.jokesapi.model.JokeSaveDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class JokeServiceTest {

    @Mock
    private JokeFetchService jokeFetchService; // Mocking JokeFetchService

    @Mock
    private JokeInsertService jokeInsertService; // Mocking JokeInsertService

    @InjectMocks
    private JokeService jokeService; // Injecting the mocks into JokeService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchAndSaveJokesTest() {
        // Prepare mock data
        JokeFetchDto jokeFetchDto1 = new JokeFetchDto("1", "Hi how are you?", "I'm fine.");
        JokeFetchDto jokeFetchDto2 = new JokeFetchDto("2", "What's up?", "Not much.");

        // Mock the behavior of the JokeFetchService
        when(jokeFetchService.fetchJokes(anyInt())).thenReturn(Flux.just(jokeFetchDto1, jokeFetchDto2));

        // Mock the behavior of the JokeInsertService
        when(jokeInsertService.insertIfNotExists(any(JokeSaveDto.class))).thenAnswer(invocation -> {
            JokeSaveDto jokeSaveDto = invocation.getArgument(0);
            return Mono.just(new JokeSaveDto(jokeSaveDto.getId(), jokeSaveDto.getQuestion(), jokeSaveDto.getAnswer()));
        });

        
        Mono<JokeApiResponse> resultMono = jokeService.fetchAndSaveJokes(2);
        
        
        JokeApiResponse result = resultMono.block();

        // Assertions
        assertEquals(2, result.getJokes().size());
        List<JokeResponseDto> jokes = result.getJokes();
        
        

        assertEquals("1", jokes.get(0).getId());
        assertEquals("Hi how are you?", jokes.get(0).getQuestion());
        assertEquals("I'm fine.", jokes.get(0).getAnswer());

        // Verify second joke
        assertEquals("2", jokes.get(1).getId());
        assertEquals("What's up?", jokes.get(1).getQuestion());
        assertEquals("Not much.", jokes.get(1).getAnswer());
        }
}