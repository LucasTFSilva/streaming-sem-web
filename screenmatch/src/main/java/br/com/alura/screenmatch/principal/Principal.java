package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.services.ConsumoAPI;
import br.com.alura.screenmatch.services.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    //Atributos e variáveis
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumo = new ConsumoAPI();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=265b7797";
    private final ConverteDados conversor = new ConverteDados();
    private final List<DadosSeries> listaDadosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

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
                    4) Buscar série por título         
                    5) Buscar séries por ator
                    6) Top 5 séries do momento
                    7) Buscar séries por categoria
                    8) Buscar séries por temporadas e avaliacao
                    9) Buscar episódio por trecho
                    10) Top 5 episódios por série
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
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop();
                    break;
                case 7:
                    buscarPorCategoria();
                    break;
                case 8:
                    buscaTemporadasMax();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscaMelhoresEpisodios();
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
        repositorio.save(serie);
        System.out.println(dados);
    }

    //Método de busca por lista de episódios
    private void buscarEpisodioPorSerie() {

        listaSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome");
        var nomeSerie = scanner.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+")
                        + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.ObterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.listaEpisodios().stream()
                            .map(e -> new Episodio(d.season(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada");
        }

    }

    //Método de exibição de histórico de séries
    private void listaSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    //Método de pequisa de série por título
    private void buscarSeriePorTitulo() {
        System.out.print("Escolha uma série pelo nome: ");
        var nomeSerie = scanner.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada");
        }
    }

    //Método de pesquisa de ator por série
    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome para busca: ");
        var nomeAtor = scanner.nextLine();

        System.out.println("Avaliações a partir de qual valor?");
        var valorAvaliacao = scanner.nextDouble();


        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoIsGreaterThanEqual
                (nomeAtor, valorAvaliacao);

        if (seriesEncontradas.isEmpty()) {
            System.out.println("Séries não encontradas!");
        } else {
            System.out.println("Séries em que " + nomeAtor + " trabalhou:");
            seriesEncontradas.forEach(s ->
                    System.out.println("Título: " + s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
        }
    }

    public void buscarTop() {
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(d ->
                System.out.println("Título: " + d.getTitulo() + " | Avaliação: " + d.getAvaliacao()));
    }

    public void buscarPorCategoria() {
        System.out.println("Digite a gênero de série que deseja buscar:");
        var pesquisaCategoria = scanner.nextLine();
        Categoria categoria = Categoria.fromPortugues(pesquisaCategoria);

        List<Serie> serieCategorias = repositorio.findByGenero(categoria);
        serieCategorias.forEach(System.out::println);
    }

    public void buscaTemporadasMax(){
        System.out.println("Você deseja assistir séries de até no máximo quantas temporadas?");
        var maxTemporadas = scanner.nextInt();

        System.out.println("Com avaliações a partir de qual valor?");
        var maxAvaliacao = scanner.nextDouble();

        List<Serie> seriesMaxTemporadas = repositorio.seriesPorTemporadaEAvaliacao(maxTemporadas, maxAvaliacao);
        seriesMaxTemporadas.forEach(System.out::println);
    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Digite o nome para busca: ");
        var trechoEpisodio = scanner.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodioPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                e.getSerie().getTitulo(), e.getTemporada(),
                e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void buscaMelhoresEpisodios(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(s -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                    s.getSerie().getTitulo(), s.getTemporada(),
                    s.getNumeroEpisodio(), s.getTitulo()));
        }
    }

}