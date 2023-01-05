package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.repository.FormaPagamentoRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepository;

@Service
public class CadastroRestauranteService {

	@Autowired
	public RestauranteRepository restauranteRepository;
	
	@Autowired
	public CozinhaRepository cozinhaRepository;
	
	@Autowired
	public FormaPagamentoRepository formaPagamentoRepository;
	
	/** Este método tando salva quanto atualiza um registro de Restaurante. * */
		
	public Restaurante salvar(Restaurante restaurante) {
		Long cozinhaId = restaurante.getCozinha().getId();
		
		Cozinha cozinha = cozinhaRepository.findById(cozinhaId)
			.orElseThrow(() -> new EntidadeNaoEncontradaException(
							String.format("Não existe cadastro de cozinha com código %d",
									cozinhaId)));
			
		restaurante.setCozinha(cozinha);
				
		return restauranteRepository.salvar(restaurante);
				
	}
	
}