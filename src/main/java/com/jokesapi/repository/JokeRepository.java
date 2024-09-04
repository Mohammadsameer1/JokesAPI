package com.jokesapi.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.jokesapi.model.Joke;

public interface JokeRepository extends ReactiveCrudRepository<Joke, String>{

}
