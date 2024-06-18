package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.services.ConsumoAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumo = new ConsumoAPI();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=265b7797";
    private final ConverteDados conversor = new ConverteDados();

    public void menu(){
        System.out.print("Digite a série que deseja pesquisar: ");
        var resposta = scanner.nextLine();
        var url = ENDERECO + resposta.replace(" ", "+") + API_KEY;
        var json = consumo.obterDados(url);
        DadosEpisodio dadosEpisodio = conversor.ObterDados(json, DadosEpisodio.class);
        System.out.println(dadosEpisodio);

        DadosSeries dados = conversor.ObterDados(json, DadosSeries.class);
        List<DadosTemporada> listaEpisodios = new ArrayList<>();

        for (int i = 1; i <= dados.temporadas(); i++){
            json = consumo.obterDados(ENDERECO + resposta.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.ObterDados(json, DadosTemporada.class);
            listaEpisodios.add(dadosTemporada);
        }
        listaEpisodios.forEach(System.out::println);
    }
}