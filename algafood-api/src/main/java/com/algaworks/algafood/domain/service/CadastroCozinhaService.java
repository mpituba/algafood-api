package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
}
