package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record SerieDTO(Long ID,
                       String titulo,
                       int temporadas,
                       double avaliacao,
                       Categoria genero,
                       String atores,
                       String sinopse,
                       String poster) {
}
