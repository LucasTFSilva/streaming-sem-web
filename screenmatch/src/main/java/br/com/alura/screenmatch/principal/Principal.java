package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSeries;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.services.ConsumoAPI;
import br.com.alura.screenmatch.services.ConverteDados;

import java.util.*;

public class Principal {

    //Atributos e variáveis
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumo = new ConsumoAPI();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=265b7797";
    private final ConverteDados conversor = new ConverteDados();
    private final List<DadosSeries> listaDadosSeries = new ArrayList<>();
    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    //Método de interface com usuário
    public void exibeMenu() {

        var resposta = -1;

        while (resposta != 0) {
            var menu = """
                    \n1) Buscar séries
                    2) Buscar episódios
                    3) Histórico de séries buscadas              
                    0) Sair
                    *********************************                                 
                    """;

            System.out.println(menu);
            resposta = scanner.nextInt();
            scanner.nextLine();

            switch (resposta) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listaSeriesBuscadas();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    //Entrada do nome da série para pesquisa em API e conversão em Dados Série
    private DadosSeries getDadosSerie() {
        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = scanner.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSeries dados = conversor.ObterDados(json, DadosSeries.class);
        return dados;
    }

    //Busca por DadosSeries
    private void buscarSerieWeb() {
        DadosSeries dados = getDadosSerie();
        Serie serie = new Serie(dados);
//        listaDadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    //Busca por lista de episódios por série e temporada
    private void buscarEpisodioPorSerie() {
        DadosSeries dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.temporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+")
                    + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.ObterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }

    private void listaSeriesBuscadas() {
        List<Serie> series = new ArrayList<>();
        series = listaDadosSeries.stream()
                .map(Serie::new)
                        .toList();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

}