package io.github.cursodsousa.msclientes.dto;

import io.github.cursodsousa.msclientes.domain.Cliente;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
@Builder
public class ClienteRequest {
    private String cpf;
    private String nome;
    private Integer idade;

    public Cliente toModel(){
        return new Cliente(cpf,nome,idade);
    }
}
