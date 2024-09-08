package com.jokesapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JokeFetchDto {
	
    private String id;
    
    @JsonProperty("setup")
    private String question;
    
    @JsonProperty("punchline")
    private String answer;
}
