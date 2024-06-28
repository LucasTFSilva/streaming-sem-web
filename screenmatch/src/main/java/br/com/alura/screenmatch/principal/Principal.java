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
    List<Serie> series = new ArrayList<>();

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
                    8) Assistir séries por número de temporadas
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
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada");
        }
    }

    //Método de pesquisa de ator por série
    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator: ");
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

        List<Serie> seriesMaxTemporadas = repositorio.findByTemporadasIsLessThanEqual(maxTemporadas);
        seriesMaxTemporadas.forEach(System.out::println);
    }

}