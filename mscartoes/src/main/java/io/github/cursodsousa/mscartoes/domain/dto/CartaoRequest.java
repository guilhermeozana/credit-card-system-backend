package io.github.cursodsousa.mscartoes.domain.dto;

import io.github.cursodsousa.mscartoes.domain.enums.BandeiraCartao;
import io.github.cursodsousa.mscartoes.domain.model.Cartao;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartaoRequest {
    private String nome;
    private BandeiraCartao bandeira;
    private BigDecimal renda;
    private BigDecimal limite;

    public Cartao toModel(){
        return new Cartao(nome, bandeira, renda, limite);
    }
}
