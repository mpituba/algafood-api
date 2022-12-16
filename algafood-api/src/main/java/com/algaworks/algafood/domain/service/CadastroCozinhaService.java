package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;

@Service
public class CadastroCozinhaService {
	
	/** Injeção de instância de repositório de Cozinha.**/
	@Autowired
	public CozinhaRepository cozinhaRepository;
	
	/** Este método tando salva quanto atualiza um registro de Cozinha. * */
	public Cozinha salvar(Cozinha cozinha) {
		
		return cozinhaRepository.salvar(cozinha);
				
	}
	
	public void excluir(Long cozinhaId) {
		
		try {
			cozinhaRepository.remover(cozinhaId);
		
		}catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de Cozinha com código %d", cozinhaId));
				
		}catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("Cozinha de código %d não pode ser removida, pois está em uso",
					cozinhaId));
		}
		
	}
	
}
