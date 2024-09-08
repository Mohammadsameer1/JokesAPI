package com.jokesapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jokesapi.model.JokeApiResponse;
import com.jokesapi.model.JokeResponseDto;
import com.jokesapi.model.JokeSaveDto;
import com.jokesapi.service.JokeFetchService;
import com.jokesapi.service.JokeInsertService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/jokes")
public class JokeController {

	@Autowired
	private JokeFetchService jokeService;  // Fetching jokes from an external service

	@Autowired
	private JokeInsertService jokeInsertService; 

	@Operation(summary = "Fetch random jokes", description = "Fetch a spe=ified number of random jokes, betwen 1 and 100. Jokes are saved if they dont already exist.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully fetched jokes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JokeApiResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalied count parameter", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })

	@GetMapping
	public Mono<ResponseEntity<JokeApiResponse>> getJokes(@RequestParam(required = true) int count) {

	    log.info("Received request to fetch {} jokes", count);

	 // Validating the cont paramter, must be between 1 and 100
	    
	    if (count < 1 || count > 100) {
	        log.warn("Invalid joke count: {}. Must be between 1 and 100", count);
	        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
	    }

	    return jokeService.fetchJokes(count).buffer(10)
	            .flatMap(jokeFetchDtos -> Flux.fromIterable(jokeFetchDtos)
	                    .flatMap(jokeFetchDto -> jokeInsertService
	                            .insertIfNotExists(new JokeSaveDto(jokeFetchDto.getId(), jokeFetchDto.getQuestion(), jokeFetchDto.getAnswer()))
	                            .onErrorResume(ex -> {
	                                log.error("Error while inserting joke: {}", ex.getMessage());
	                                return Mono.error(ex); // Propagate the error upwards
	                            }))
	                    .map(jokeSaveDto -> new JokeResponseDto(jokeSaveDto.getId(), jokeSaveDto.getQuestion(), jokeSaveDto.getAnswer()))) // Mapping to response DTO
	            .collectList()
	            .map(jokesList -> new JokeApiResponse(jokesList)) // Create a JokeApiResponse with the list of jokes
	            .map(jokeApiResponse -> ResponseEntity.ok(jokeApiResponse)) // return 200 reponse
	            .onErrorResume(ex -> {
	                log.error("Exception occurred: {}", ex.getMessage());
	                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
	            }); // Return INTERNAL_SERVER_ERROR when an error occurs
	}
}
