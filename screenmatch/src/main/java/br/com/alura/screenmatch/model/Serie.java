package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.services.traducao.ConsultaMyMemory;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series")
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private int temporadas;
    private double avaliacao;

    @Enumerated(EnumType.STRING)
    private Categoria genero;

    private String atores;
    private String sinopse;
    private String poster;

    @Transient
    private List<Episodio> episodiosList = new ArrayList<>();

    public Serie (DadosSeries dadosSeries){
        this.titulo = dadosSeries.titulo();
        this.temporadas = dadosSeries.temporadas();
        this.avaliacao = dadosSeries.avaliacao();
        this.genero = Categoria.fromString(dadosSeries.genero().split(",")[0].trim());
        this.atores = dadosSeries.atores();
        this.sinopse = ConsultaMyMemory.obterTraducao(dadosSeries.sinopse().trim());
        this.poster = dadosSeries.poster();
    }

    public Serie() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getTemporadas() {
        return temporadas;
    }

    public void setTemporadas(int temporadas) {
        this.temporadas = temporadas;
    }

    public double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public String toString() {
        return "\nTitulo: " + titulo +
                "\nTemporadas: " + temporadas +
                "\nAvaliacao: " + avaliacao +
                "\nGênero: " + genero +
                "\nAtores: " + atores +
                "\nSinopse: " + sinopse +
                "\nPoster: " + poster;
    }

}
