package com.jokesapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jokesapi.model.Joke;
import com.jokesapi.service.JokeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/jokes")
public class JokeController {

	@Autowired
	private JokeService jokeService;

	public JokeController(JokeService jokeService) {
		this.jokeService = jokeService;
	}

	@GetMapping
	public ResponseEntity<Flux<Joke>> getJokes(@RequestParam(required = true) int count) {

		log.info("Received request to fetch {} jokes", count);

		if (count < 1 || count > 100) {
			log.warn("Invalid joke count:{}.Must be between 1 and 100 ", count);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		}

		Flux<Joke> fetchAndSaveJokes = jokeService.fetchAndSaveJokes(count);

		log.info("Successfully fetched {} jokes", count);

		return ResponseEntity.ok(fetchAndSaveJokes);
	}

}
