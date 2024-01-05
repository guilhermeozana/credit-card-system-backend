package io.github.cursodsousa.mscartoes.controller;

import io.github.cursodsousa.mscartoes.domain.model.Cartao;
import io.github.cursodsousa.mscartoes.domain.model.ClienteCartao;
import io.github.cursodsousa.mscartoes.domain.dto.CartaoClienteDTO;
import io.github.cursodsousa.mscartoes.domain.dto.CartaoRequest;
import io.github.cursodsousa.mscartoes.service.CartaoService;
import io.github.cursodsousa.mscartoes.service.ClienteCartaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("cartoes")
public class CartoesController {
    private final CartaoService cartaoService;
    private final ClienteCartaoService clienteCartaoService;


    @GetMapping
    public String status(){
        return "ok";
    }

    @PostMapping
    public ResponseEntity cadastra(@RequestBody CartaoRequest cartaoRequest){
        Cartao cartao = cartaoRequest.toModel();
        cartaoService.save(cartao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda){
        List<Cartao> cartoesRendaMenorIgual = cartaoService.getCartoesRendaMenorIgual(renda);

        return ResponseEntity.ok(cartoesRendaMenorIgual);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<CartaoClienteDTO>> getCartoesByCliente(@RequestParam("cpf")  String cpf){
        List<ClienteCartao> listaClienteCartao = clienteCartaoService.listCartoesByCpf(cpf);

        List<CartaoClienteDTO> resultList = listaClienteCartao.stream()
                .map(CartaoClienteDTO::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultList);
    }
}
