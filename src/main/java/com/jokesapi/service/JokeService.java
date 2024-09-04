package com.jokesapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.jokesapi.model.Joke;
import com.jokesapi.repository.JokeRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class JokeService {

	private WebClient webClient;

	private JokeRepository jokeRepository;

	@Autowired
	public JokeService(WebClient webClient, JokeRepository jokeRepository) {

		this.webClient = webClient;
		this.jokeRepository = jokeRepository;
	}

	public Flux<Joke> fetchAndSaveJokes(int count) {

		log.info("Starting to fetch and save {} jokes",count);

		return Flux.range(1, count)
				.flatMap(i -> webClient.get()
				.uri("https://official-joke-api.appspot.com/random_joke")
				.retrieve()
				.bodyToMono(Joke.class)
				.doOnNext(joke -> log.debug("Fetched joke: {} ",joke))
				.flatMap(jokeRepository::save))
				.buffer(10)
				.flatMap(Flux::fromIterable);

	}

}
