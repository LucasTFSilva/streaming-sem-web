package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.services.ConsumoAPI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Principal{

    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumo = new ConsumoAPI();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=265b7797";
    private final ConverteDados conversor = new ConverteDados();

    public void menu() {
        System.out.print("Digite a série que deseja pesquisar: ");
        var resposta = scanner.nextLine();
        var url = ENDERECO + resposta.replace(" ", "+") + API_KEY;
        var json = consumo.obterDados(url);
        DadosSeries dados = conversor.ObterDados(json, DadosSeries.class);
        System.out.println(dados);

        List<DadosTemporada> listaTemporadas = new ArrayList<>();

        for (int i = 1; i <= dados.temporadas(); i++) {
            json = consumo.obterDados(ENDERECO + resposta.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.ObterDados(json, DadosTemporada.class);
            listaTemporadas.add(dadosTemporada);
        }

//        listaTemporadas.forEach(t -> t.listaEpisodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> listaEpisodios = listaTemporadas.stream()
                .flatMap(t -> t.listaEpisodios().stream())
                .toList();

        System.out.println("\nTop 5 episódios: ");
        listaEpisodios.stream()
                .filter(f -> !"N/A".equalsIgnoreCase(f.avaliacao()))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);
    }
}