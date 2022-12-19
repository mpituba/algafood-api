package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.repository.FormaPagamentoRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepository;

@Service
public class CadastroRestauranteService {

	@Autowired
	public RestauranteRepository restauranteRepository;
	
	@Autowired
	public CozinhaRepository cozinhaRespository;
	
	@Autowired
	public FormaPagamentoRepository formaPagamentoRepository;
	
	public Restaurante salvar(Restaurante restaurante) {
		
		Long cozinhaId = restaurante.getCozinha().getId();
		Cozinha cozinha = cozinhaRespository.buscar(cozinhaId);
		if (cozinha == null) {
			throw new EntidadeNaoEncontradaException(
				 String.format("Não existe cadastro de Cozinha com código %d", cozinhaId));
		}
		
		Long formaPagamentoId = restaurante.getFormasPagamento().getId();
		FormaPagamento	formaPagamento = formaPagamentoRepository.buscar(formaPagamentoId);
		if (formaPagamento == null) {
			throw new EntidadeNaoEncontradaException(
				 String.format("Não existe cadastro de FormaPagamento com código %d", formaPagamentoId));
		}
							
		restaurante.setCozinha(cozinha);
		restaurante.setFormasPagamento(formaPagamento);
			
		return restauranteRepository.salvar(restaurante);
		
	}
	
}
