package io.github.cursodsousa.msavaliadorcredito.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import io.github.cursodsousa.msavaliadorcredito.ProtocoloSolicitacaoCartao;
import io.github.cursodsousa.msavaliadorcredito.client.CartoesClient;
import io.github.cursodsousa.msavaliadorcredito.client.ClienteClient;
import io.github.cursodsousa.msavaliadorcredito.exception.DadosClienteNotFoundException;
import io.github.cursodsousa.msavaliadorcredito.exception.ErroComunicacaoMicroservicesException;
import io.github.cursodsousa.msavaliadorcredito.exception.ErroSolicitacaoCartaoException;
import io.github.cursodsousa.msavaliadorcredito.model.domain.*;
import io.github.cursodsousa.msavaliadorcredito.model.dto.DadosSolicitacaoEmissaoCartao;
import io.github.cursodsousa.msavaliadorcredito.mqueue.SolicitacaoEmissaoCartaoPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {
    private final ClienteClient clientesClient;
    private final CartoesClient cartoesClient;
    private final SolicitacaoEmissaoCartaoPublisher solicitacaoEmissaoCartaoPublisher;
    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException,
            ErroComunicacaoMicroservicesException {

        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();

        } catch(FeignException.FeignClientException e){
            int status = e.status();

            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }

            throw new ErroComunicacaoMicroservicesException(e.getMessage(), e.status());
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClienteNotFoundException,
            ErroComunicacaoMicroservicesException{
        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAte(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();

            var listaCartoesAprovados = cartoes.stream().map(cartao -> {
                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal rendaBD = BigDecimal.valueOf(renda);
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                var fator = idadeBD.divide(BigDecimal.valueOf(10));

                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado aprovado = new CartaoAprovado();

                aprovado.setCartao(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeira());
                aprovado.setLimiteAprovado(limiteAprovado);

                return aprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listaCartoesAprovados);

        } catch(FeignException.FeignClientException e){
            int status = e.status();

            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }

            throw new ErroComunicacaoMicroservicesException(e.getMessage(), e.status());
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try {
            solicitacaoEmissaoCartaoPublisher.solicitarCartao(dados);

            var protocolo = UUID.randomUUID().toString();

            return new ProtocoloSolicitacaoCartao(protocolo);
        }catch (Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }
}
