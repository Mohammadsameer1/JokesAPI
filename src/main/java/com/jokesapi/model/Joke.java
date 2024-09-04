package com.jokesapi.model;

import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("jokes")
public class Joke {


	private String id;
	@JsonProperty("setup")
	private String question;
	@JsonProperty("punchline")
	private String answer;

}
