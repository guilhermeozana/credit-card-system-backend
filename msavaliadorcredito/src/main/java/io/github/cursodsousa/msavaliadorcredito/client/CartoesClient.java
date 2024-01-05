package io.github.cursodsousa.msavaliadorcredito.client;

import io.github.cursodsousa.msavaliadorcredito.model.domain.Cartao;
import io.github.cursodsousa.msavaliadorcredito.model.domain.CartaoCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "mscartoes", path = "/cartoes")
public interface CartoesClient {

    @GetMapping(params = "cpf")
    public ResponseEntity<List<CartaoCliente>> getCartoesByCliente(@RequestParam("cpf")  String cpf);

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda);
}
