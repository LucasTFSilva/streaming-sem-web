package br.com.alura.screenmatch.model;

public interface IConverteDados {
    <T> T ObterDados(String json, Class<T> classe);
}
