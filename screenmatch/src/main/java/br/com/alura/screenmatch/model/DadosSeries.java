package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public record DadosSeries(@JsonAlias("Title") String titulo,
                          @JsonAlias("imdbRating") double avaliacao,
                          @JsonAlias("totalSeasons") String temporadas) {
}
