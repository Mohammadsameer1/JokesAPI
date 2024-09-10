package com.jokesapi.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jokesapi.model.JokeApiResponse;
import com.jokesapi.model.JokeResponseDto;
import com.jokesapi.model.JokeSaveDto;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class JokeService {

	@Autowired
	private JokeFetchService jokeFetchService; // Fetching jokes from an external service

	@Autowired
	private JokeInsertService jokeInsertService;

	public Mono<JokeApiResponse> fetchAndSaveJokes(int count) {

		return jokeFetchService.fetchJokes(count).buffer(10)
				.flatMap(jokeFetchDtos -> Flux.fromIterable(jokeFetchDtos)
				.flatMap(jokeFetchDto -> jokeInsertService
						.insertIfNotExists(new JokeSaveDto(jokeFetchDto.getId(), jokeFetchDto.getQuestion(), jokeFetchDto.getAnswer()))
						.onErrorResume(ex -> {
                            log.error("error while inserting joke: {}",ex.getMessage());
							return Mono.error(ex);
						}))
				.map(jokeSaveDto -> new JokeResponseDto(jokeSaveDto.getId(), jokeSaveDto.getQuestion(),jokeSaveDto.getAnswer()))

		).collectList().map(JokeApiResponse::new);

	}

}
