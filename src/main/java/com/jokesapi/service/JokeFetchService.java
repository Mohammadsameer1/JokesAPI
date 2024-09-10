package com.jokesapi.service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.jokesapi.exception.InvalidJokeResponseException;
import com.jokesapi.exception.JokeFetchException;
import com.jokesapi.model.JokeFetchDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service

public class JokeFetchService {

	  Logger log= LoggerFactory.getLogger(JokeFetchService.class);

	
    @Autowired
    private WebClient webClient;  // Autowired WebClient used for fetching jokes from external API

 // Method to fetch jokes, takes the count of how many jokes to fecth
    public Flux<JokeFetchDto> fetchJokes(int count) {
        log.info("Starting to fetch and save {} jokes", count);

        return Flux.range(1, count)
                .flatMap(i -> webClient.get()
                    .uri("https://official-joke-api.appspot.com/random_joke")
                    .retrieve()
                    .bodyToMono(JokeFetchDto.class)
                    .doOnSuccess(joke -> log.info("Successfully fetched joke from fetch service: {}", joke))
                    .doOnError(ex -> {
                        log.error("Error fetching joke: {}", ex.getMessage());
                        throw new JokeFetchException("Failed to fetch joke from server: " + ex.getMessage());
                    })
                   
                )
                .flatMap(jokeFetchDto -> {

                	// Check if the fetched joke has invalid data
                    if (jokeFetchDto == null || jokeFetchDto.getQuestion() == null || jokeFetchDto.getAnswer() == null) {
                        log.error("Received invalid joke response: {}", jokeFetchDto);
                        throw new InvalidJokeResponseException("Invalid joke response: " + jokeFetchDto);
                    }
                    return Mono.just(jokeFetchDto);
                });
    }
}