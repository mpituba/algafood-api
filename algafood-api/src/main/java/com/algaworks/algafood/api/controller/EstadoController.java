package com.algaworks.algafood.api.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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

import com.algaworks.algafood.domain.repository.EstadoRepository;
import com.algaworks.algafood.domain.service.CadastroEstadoService;
import com.algaworks.algafood.domain.exception.EntidadeEmConflitoException;
import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Estado;

@RestController
@RequestMapping("/estados")
public class EstadoController {
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private CadastroEstadoService cadastroEstado;
	
	@GetMapping
	public List<Estado> listar() {
		return estadoRepository.listar();
	}
	
	@GetMapping("/{estadoId}")
	public ResponseEntity<Estado> buscar (@PathVariable Long estadoId) {
		
		Estado estado = estadoRepository.buscar(estadoId);
		
		if (estado == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(estado);
	}
	
	@PostMapping
	public ResponseEntity<?> adicionar(@RequestBody Estado estado) {
		
		try {
			
				if ((estado.getNome() == null) || estado.getNome().isEmpty()) {
					throw new EntidadeNaoEncontradaException( 
							String.format("O Nome do Estado não foi informado"));
				}
				
				Estado estadoSalvo = cadastroEstado.salvar(estado);
				return ResponseEntity.status(HttpStatus.CREATED).body(estadoSalvo);
				
		
		}catch(EntidadeNaoEncontradaException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
				
		}
	}
	
	@PutMapping("/{estadoId}")
	public ResponseEntity<?> atualizar (@PathVariable Long estadoId,
			@RequestBody Estado estado) {
		
		Estado estadoAtual = estadoRepository.buscar(estadoId);
		
		try {
			
			/** Verifica se o Id do corpo da requisição existe  **/
			if (estado.getId() == null ) {
				
				throw new EntidadeNaoEncontradaException(
						String.format("O Id do Estado não foi informado"));
			}
			
			/** Verifica se o Id da URI é iqual ao do corpo da requisição **/
			if (estado.getId() != estadoId) {
				
				throw new EntidadeEmConflitoException(
						String.format("O Id  %d da URI difere do Id %d do corpo da requisição",
										estadoId, estado.getId()));
				
			}
			
			/** Verifica se a propriedade Nome do Estado está no corpo da requisição **/
			if ((estado.getNome() == null) || estado.getNome().isEmpty()){
				
				throw new EntidadeNaoEncontradaException(
						String.format("O Nome do Estado não foi informado"));
			}
						
			
			if (estadoAtual == null) {

				return ResponseEntity.notFound().build();
			}
			
			BeanUtils.copyProperties(estado, estadoAtual, "estadoId");
			
			estadoAtual = cadastroEstado.salvar(estadoAtual);
			return ResponseEntity.ok(estadoAtual);
						
		}catch (EntidadeNaoEncontradaException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
		
		}catch (EntidadeEmConflitoException e) {
			
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
			
		}
	}
	
	@DeleteMapping("/{estadoId}")
	public ResponseEntity<?> remover(@PathVariable Long estadoId) {
		
		Estado estado = estadoRepository.buscar(estadoId);
		
		try {
			
			if (estado == null) {
				
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
				
			}
						
			cadastroEstado.excluir(estadoId);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
						
		}catch (EntidadeNaoEncontradaException e) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
					
		}catch (EmptyResultDataAccessException e) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			
		}catch (DataIntegrityViolationException e) {
			
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
			
		}catch (EntidadeEmUsoException e) {
			
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
			
		}
	}
		
}
