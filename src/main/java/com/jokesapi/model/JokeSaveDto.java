package com.jokesapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JokeSaveDto {


	private String id;

	private String question;

	private String answer;
}
