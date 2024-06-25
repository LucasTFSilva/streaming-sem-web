package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public record DadosSeries(@JsonAlias("Title") String titulo,
                          @JsonAlias("totalSeasons") int temporadas,
                          @JsonAlias("imdbRating") double avaliacao,
                          @JsonAlias("Genre") String genero,
                          @JsonAlias("Actors") String atores,
                          @JsonAlias("Plot") String sinopse,
                          @JsonAlias("Poster") String poster)
{
}
