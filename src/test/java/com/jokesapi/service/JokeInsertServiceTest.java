package com.jokesapi.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveInsertOperation.ReactiveInsert;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation.ReactiveSelect;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation.TerminatingSelect;
import org.springframework.data.relational.core.query.Query;

import com.jokesapi.exception.DataAccessException;
import com.jokesapi.model.JokeEntity;
import com.jokesapi.model.JokeSaveDto;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

 class JokeInsertServiceTest {

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Mock
    private ReactiveSelect<JokeEntity> reactiveSelect;

    @Mock
    private TerminatingSelect<JokeEntity> terminatingSelect;

    @Mock
    private ReactiveInsert<JokeEntity> reactiveInsert;  // Mock the ReactiveInsert

    @InjectMocks
    private JokeInsertService jokeInsertService;

    private JokeSaveDto jokeSaveDto;
    private JokeEntity jokeEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jokeSaveDto = new JokeSaveDto("1", "Why did the chicken cross the road?", "To get to the other side!");
        jokeEntity = new JokeEntity("1", "Why did the chicken cross the road?", "To get to the other side!");

        setUpSelectMock(); // Reuse this method for mocking select
    }

    private void setUpSelectMock() {
        when(r2dbcEntityTemplate.select(JokeEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
    }

    private void setUpInsertMock() {
        when(r2dbcEntityTemplate.insert(JokeEntity.class)).thenReturn(reactiveInsert);
    }

   

   
    
    
    
    @Test
    void successfulInsertTest() {
        JokeSaveDto newJokeSaveDto = new JokeSaveDto("1", "Hi good morning?", "Very good morning");

        when(terminatingSelect.one()).thenReturn(Mono.empty());
        setUpInsertMock();  // Reuse this method for mocking insert
        when(reactiveInsert.using(any(JokeEntity.class))).thenReturn(Mono.just(new JokeEntity(newJokeSaveDto.getId(), newJokeSaveDto.getQuestion(), newJokeSaveDto.getAnswer())));

        Mono<JokeSaveDto> result = jokeInsertService.insertIfNotExists(newJokeSaveDto);

        assertEquals(newJokeSaveDto, result.block());
    }

    @Test
     void testInsertIfNotExists_DatabaseInsertionError() {
        when(terminatingSelect.one()).thenReturn(Mono.empty());
        setUpInsertMock();  // Reuse this method for mocking insert
        when(reactiveInsert.using(any(JokeEntity.class))).thenReturn(Mono.error(new RuntimeException("Insertion error")));

        Mono<JokeSaveDto> result = jokeInsertService.insertIfNotExists(jokeSaveDto);

        StepVerifier.create(result)
                .expectError(DataAccessException.class)
                .verify();

        verify(r2dbcEntityTemplate).insert(JokeEntity.class); // Verify that the insert operation is called
    }
}
