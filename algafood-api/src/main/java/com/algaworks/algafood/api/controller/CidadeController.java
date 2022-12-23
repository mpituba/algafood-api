package com.algaworks.algafood.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.exception.EntidadeEmConflitoException;
import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.repository.CidadeRepository;
import com.algaworks.algafood.domain.service.CadastroCidadeService;

@RestController
@RequestMapping("/cidades")
public class CidadeController {

	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private CadastroCidadeService cadastroCidade;
	
	@GetMapping
	public ResponseEntity<List<Cidade>> listar() {
		
		List<Cidade> cidadeList = new ArrayList<Cidade>();
		
		cidadeList = cidadeRepository.listar();
		
		return ResponseEntity.ok(cidadeList);
	}
	
	
	@GetMapping("/{cidadeId}")
	public ResponseEntity<?> buscar (@PathVariable Long cidadeId) {
		
		Cidade cidade = cidadeRepository.buscar(cidadeId);
		
		if (cidade == null) {
			
			return ResponseEntity.notFound().build();
			
		}
		
		return ResponseEntity.ok(cidade);
		
	}
	
	@PostMapping
	public ResponseEntity<?> adicionar(@RequestBody Cidade cidade) {
		
		try {
			
			if (cidade == null) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("A Cidade deve ser informada."));
			}
			
			if ((cidade.getNome() == null) || cidade.getNome().isEmpty()) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("O Nome da Cidade deve ser informado."));
				
			}
			
			if (cidade.getEstado() == null) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("O Estado deve ser informado."));
				
			}
			
			if ((cidade.getEstado().getId() == null) || (cidade.getEstado().getNome() == null)) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("O Id ou Nome do Estado para a Cidade devem ser informados."));
				
			}
			
			Cidade cidadeSalva = cadastroCidade.salvar(cidade);
			return ResponseEntity.status(HttpStatus.CREATED).body(cidadeSalva);
		
		}catch (EntidadeNaoEncontradaException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
	}
	
	@PutMapping("/{cidadeId}")
	public ResponseEntity<?> atualizar(@PathVariable Long cidadeId, @RequestBody Cidade cidade) {
		
		Cidade cidadeAtual = cidadeRepository.buscar(cidadeId);
		
		try { 
			
			if (cidade == null) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("A Cidade deve ser informada."));
				
			}
					
			if (cidade.getId() == null) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("O Id da Cidade deve ser informado."));
				
			} 		
			
			if ((cidade.getNome() == null) || (cidade.getNome().isEmpty())) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("O Nome da cidade deve ser informado."));
				
			}
			
			if (cidade.getEstado() == null) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("O Estado deve ser informado."));
				
			}
			
			if ((cidade.getEstado().getNome() == null) ||
			  (cidade.getEstado().getNome().isEmpty()) ||
			  (cidade.getEstado().getId() == null)) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("O Nome e Id do Estado para esta Cidade, devem ser informados."));
				
			}
			
			
			if (cidadeId != cidade.getId()) {
				
				throw new EntidadeEmConflitoException(
						String.format("O Id %d na URI, não é o mesmo informado no corpo da requisição %d.",
								cidadeId, cidade.getId()));
				
			}
			
						
			if (cidadeAtual == null) {
				
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
						String.format("A Cidade de Id %d não existe.", cidadeId));
								
			}
			
			BeanUtils.copyProperties(cidade, cidadeAtual, "cidadeId");
			
			cidadeAtual = cadastroCidade.salvar(cidadeAtual);
			
			return ResponseEntity.ok(cidade);
			
		}catch (EntidadeEmConflitoException e) {
			
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
			
		}catch (EntidadeNaoEncontradaException e) {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			
		}	
		
	}
	
	@DeleteMapping("/{cidadeId}")
	public ResponseEntity<?> excluir (@PathVariable Long cidadeId) {
		
		Cidade cidade = cidadeRepository.buscar(cidadeId);
		
		try {
		
			if (cidade == null) {
				
				ResponseEntity.status(HttpStatus.NOT_FOUND).build();			
			}
			
			cadastroCidade.excluir(cidadeId);
			
			return ResponseEntity.noContent().build();
		
		}catch (EntidadeEmUsoException e) {
			
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
			
		}catch (EntidadeNaoEncontradaException e) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		
		
	}
	
}
