package br.com.alura.screenmatch.services;

public interface IConverteDados {
    <T> T ObterDados(String json, Class<T> classe);
}
