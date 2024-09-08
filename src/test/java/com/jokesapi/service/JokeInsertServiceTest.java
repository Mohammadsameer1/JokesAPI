package com.jokesapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveInsertOperation;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;

import com.jokesapi.model.JokeEntity;
import com.jokesapi.model.JokeSaveDto;

import reactor.core.publisher.Mono;
@SuppressWarnings("unchecked")
class JokeInsertServiceTest {

	@InjectMocks
	private JokeInsertService jokeInsertService;

	@Mock
	private R2dbcEntityTemplate r2dbcEntityTemplate;

	@Mock
	private ReactiveSelectOperation.ReactiveSelect<JokeEntity> reactiveSelect;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	
	@Test
	void successfulInsertTest() {
		JokeSaveDto jokeSaveDto = new JokeSaveDto("1", "Hi good morning?",
				"very good morning");

		when(r2dbcEntityTemplate.select(JokeEntity.class)).thenReturn(reactiveSelect);
		when(reactiveSelect.matching(any(Query.class))).thenReturn(reactiveSelect);
		when(reactiveSelect.one()).thenReturn(Mono.empty());

		
		ReactiveInsertOperation.ReactiveInsert<JokeEntity> reactiveInsert = mock(
				ReactiveInsertOperation.ReactiveInsert.class);
		when(r2dbcEntityTemplate.insert(JokeEntity.class)).thenReturn(reactiveInsert);
		when(reactiveInsert.using(any(JokeEntity.class))).thenReturn(
				Mono.just(new JokeEntity(jokeSaveDto.getId(), jokeSaveDto.getQuestion(), jokeSaveDto.getAnswer())));

		Mono<JokeSaveDto> result = jokeInsertService.insertIfNotExists(jokeSaveDto);

		assertEquals(jokeSaveDto, result.block());
	}

}