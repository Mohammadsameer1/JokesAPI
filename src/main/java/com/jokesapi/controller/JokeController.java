package com.jokesapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jokesapi.model.JokeApiResponse;
import com.jokesapi.service.JokeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/jokes")
public class JokeController {

	@Autowired
	private JokeService jokeService;

	@Operation(summary = "Fetch random jokes", description = "Fetch a spe=ified number of random jokes, betwen 1 and 100..")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully fetched jokes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JokeApiResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalied count parameter", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })

	@GetMapping
	public Mono<ResponseEntity<JokeApiResponse>> getJokes(@RequestParam(required = true) int count) {

		log.info("Received request to fetch {} jokes", count);

		// Validating the cont paramter must be between 1 and 100

		if (count < 1 || count > 100) {
			log.warn("Invalid joke count: {}. Must be between 1 and 100", count);
			return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
		}

		return jokeService.fetchAndSaveJokes(count).map(ResponseEntity::ok).onErrorResume(ex -> {

			log.error("Excepttion occurred: {}", ex.getMessage());

			return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
		});

	}

}
