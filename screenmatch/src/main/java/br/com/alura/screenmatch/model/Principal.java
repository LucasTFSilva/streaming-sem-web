package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.services.ConsumoAPI;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumo = new ConsumoAPI();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=265b7797";
    private final ConverteDados conversor = new ConverteDados();

    public void menu() {
        //Abrir interface com usuário
        System.out.print("Digite a série que deseja pesquisar: ");
        var resposta = scanner.nextLine();
        var url = ENDERECO + resposta.replace(" ", "+") + API_KEY;

        //Receber e converter Json em DadosSeries.class, resgatando dados específicos
        var json = consumo.obterDados(url);
        DadosSeries dados = conversor.ObterDados(json, DadosSeries.class);
        System.out.println(dados);

        //Criar uma lista de episódios por temporada resgatando o Json e convertendo para Dados.Temporadas.class
        List<DadosTemporada> listaTemporadas = new ArrayList<>();

        for (int i = 1; i <= dados.temporadas(); i++) {
            json = consumo.obterDados(ENDERECO + resposta.replace(" ", "+") + "&season="
                    + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.ObterDados(json, DadosTemporada.class);
            listaTemporadas.add(dadosTemporada);
        }

        //Percorre os elementos de listaTemporadas, seleciona os elementos de listaEpisodio e imprime
//        listaTemporadas.forEach(t -> t.listaEpisodios().forEach(e -> System.out.println(e.titulo())));

//        List<DadosEpisodio> listaEpisodios = listaTemporadas
//                .stream()
//                .flatMap(t -> t.listaEpisodios()
//                        .stream())
//                .toList();

        //Inicia uma stream e aplica operações intermediárias para filtrar, organizar e limitar o fluxo de dados.
////        System.out.println("\nTop 5 episódios: ");
////        listaEpisodios.stream()
////                .filter(f -> !"N/A".equalsIgnoreCase(f.avaliacao()))
////                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
////                .limit(5)
////                .forEach(System.out::println);


        //Cria novo objeto apartir de Episodio.class, resgatando dados de listaEpisodios.
        List<Episodio> episodios = listaTemporadas
                .stream()
                .flatMap(t -> t.listaEpisodios().stream()
                        .map(d -> new Episodio(t.season(), d)))
                .toList();

        episodios.forEach(System.out::println);

        //Entrada interface com usuário
        System.out.println("A partir de qual ano deseja ver os episódios?");
        var ano = scanner.nextInt();
        scanner.nextLine();

        //Criando uma variável do tipo LocalDate e definindo um formato de data;
        LocalDate localDate = LocalDate.of(ano, 1, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        //Filtragem de dados pela variável localDate
        episodios.stream()
                .filter(f -> f.getDataLancamento() != null && f.getDataLancamento().isAfter(localDate))
                .forEach(f -> System.out.println(
                        "Temporada: " + f.getTemporada() +
                                " Episodio " + f.getNumeroEpisodio() +
                                " Data de Lançamento: " + f.getDataLancamento().format(formatter)
                ));
    }
}