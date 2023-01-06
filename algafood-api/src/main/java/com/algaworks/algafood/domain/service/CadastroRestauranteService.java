package com.algaworks.algafood.domain.service;



import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
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
		
		/**	
		 * Neste comando o retorno pode ser uma Entidade se encontrado, ou um
		 * Optional vazio se não encontrar nada. Por isso é que uma Cozinha é
		 * instanciada e não um Optional de Cozinha.
		**/
		Cozinha cozinha = cozinhaRepository.findById(cozinhaId)
			.orElseThrow(() -> new EntidadeNaoEncontradaException(
							String.format("Não existe cadastro de cozinha com código %d",
									cozinhaId)));
			
		restaurante.setCozinha(cozinha);
				
		return restauranteRepository.save(restaurante);
				
	}
	
	
	public void excluir(Long restauranteId) {
		
		Optional<Restaurante> restaurante = restauranteRepository.findById(restauranteId);
		
		try {
			restauranteRepository.deleteById(restauranteId);
			
		
		}catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de Restaurante com código %d .", restauranteId));
				
		}catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("O Restaurante de Id: %d e Nome: %s, não pode ser removido, pois está em uso.",
							restauranteId, restaurante.get().getNome()));
		}
		
	}
	
}