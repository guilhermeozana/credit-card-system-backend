package io.github.cursodsousa.mscartoes.domain.dto;

import io.github.cursodsousa.mscartoes.domain.model.ClienteCartao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartaoClienteDTO {
    private String nome;
    private String bandeira;
    private BigDecimal limiteLiberado;

    public static CartaoClienteDTO fromModel(ClienteCartao model){
        return new CartaoClienteDTO(
                model.getCartao().getNome(),
                model.getCartao().getBandeira().toString(),
                model.getLimite()
        );
    }
}
