package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.ConverteDados;
import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSeries;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.services.ConsumoAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoAPI consumoAPI = new ConsumoAPI();
		var json = consumoAPI.obterDados("https://www.omdbapi.com/?t=breaking+bad&apikey=265b7797");
		System.out.println(json);

		ConverteDados conversor = new ConverteDados();
		DadosSeries dados = conversor.ObterDados(json, DadosSeries.class);
		System.out.println(dados);

		json = consumoAPI.obterDados("https://www.omdbapi.com/?t=breaking+bad&season=1&episode=2&apikey=265b7797");
		DadosEpisodio dadosEpisodio = conversor.ObterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);

		List<DadosTemporada> listaEpisodios = new ArrayList<>();

		for (int i = 1; i <= dados.temporadas(); i++){
			json = consumoAPI.obterDados("https://www.omdbapi.com/?t=breaking+bad&season=" + i + "&apikey=265b7797");
			DadosTemporada dadosTemporada = conversor.ObterDados(json, DadosTemporada.class);
			listaEpisodios.add(dadosTemporada);
		}
		listaEpisodios.forEach(System.out::println);
	}
}
