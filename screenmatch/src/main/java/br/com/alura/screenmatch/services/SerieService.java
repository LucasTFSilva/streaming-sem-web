package br.com.alura.screenmatch.services;

import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repositorio;

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTemporadas(), s.getAvaliacao(), s.getGenero(),
                        s.getAtores(), s.getSinopse(), s.getPoster()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterSeries() {
        return converteDados(repositorio.findAll());
    }

    public List<SerieDTO> obterTop5() {
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converteDados(repositorio.findTop5ByOrderByEpisodiosDataLancamento());
    }

    public SerieDTO obterPorID(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTemporadas(), s.getAvaliacao(), s.getGenero(),
                    s.getAtores(), s.getSinopse(), s.getPoster());
        } else {
            return null;
        }
    }
}


