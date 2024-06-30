package br.com.alura.screenmatch.dto;

public record EpisodioDTO(Long id,
                          String titulo,
                          Integer temporada,
                          Integer numeroEpisodio) {
}
