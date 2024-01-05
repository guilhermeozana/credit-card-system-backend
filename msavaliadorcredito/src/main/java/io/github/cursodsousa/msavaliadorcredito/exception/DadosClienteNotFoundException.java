package io.github.cursodsousa.msavaliadorcredito.exception;

import lombok.Getter;

public class DadosClienteNotFoundException extends Exception {

    public DadosClienteNotFoundException() {
        super("Dados do cliente não encontrado para o cpf informado.");
    }
}
