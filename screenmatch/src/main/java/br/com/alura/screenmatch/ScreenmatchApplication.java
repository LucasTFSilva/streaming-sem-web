package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.ConverteDados;
import br.com.alura.screenmatch.model.DadosSeries;
import br.com.alura.screenmatch.services.ConsumoAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoAPI consumoAPI = new git ConsumoAPI();
		var json = consumoAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=265b7797");
		ConverteDados conversor = new ConverteDados();
		DadosSeries dados = conversor.ObterDados(json, DadosSeries.class);
		System.out.println(dados);
	}
}
