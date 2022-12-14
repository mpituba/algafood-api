package com.algaworks.algafood.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.EstadoRepository;

@Service
public class CadastroEstadoService {
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	
	public Estado salvar(Estado estado) {
		
		return estadoRepository.save(estado);
	}
	
	
	public void excluir(Long estadoId) {
			
		Optional<Estado> estado = estadoRepository.findById(estadoId);
		
		try {
			estadoRepository.deleteById(estadoId);
			
		
		}catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de Estado com código %d .", estadoId));
				
		}catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("O Estado de Id: %d e Nome: %s, não pode ser removido, pois está em uso.",
							estadoId, estado.get().getNome()));
		}
		
	}
	
}
