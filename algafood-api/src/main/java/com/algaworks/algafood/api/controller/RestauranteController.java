package com.algaworks.algafood.api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.ReflectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.repository.FormaPagamentoRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {
	
	@Autowired
	public RestauranteRepository restauranteRepository;
	
	@Autowired
	public FormaPagamentoRepository formaPagamentoRepository;
	
	@Autowired
	public CozinhaRepository cozinhaRepository;
	
	@Autowired
	public CadastroRestauranteService cadastroRestaurante;
	
	
	@GetMapping
	public ResponseEntity<List<Restaurante>> listar () {
		
		return ResponseEntity.status(HttpStatus.OK).body(restauranteRepository.findAll());
		
	}
	
	@GetMapping("/{restauranteId}")
	public ResponseEntity<Restaurante> buscar (@PathVariable Long restauranteId) {
		
		Optional<Restaurante> restaurante = restauranteRepository.findById(restauranteId);
		
		if (restaurante.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(restaurante.get());
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> adicionar (@RequestBody Restaurante restaurante) {
		
		try {
			
			restaurante = cadastroRestaurante.salvar(restaurante);
			return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);
			
		}catch (EntidadeNaoEncontradaException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
		
	}
	
	
	@PutMapping("/{restauranteId}")
	public ResponseEntity<?> atualizar(@PathVariable Long restauranteId,
			@RequestBody Restaurante restaurante) {
		
		
		try {
			
			/** Verifica se o Id foi informado na requisição.**/
			if (restaurante.getId() == null) {
				throw new 	EntidadeNaoEncontradaException(
						 String.format("O id do Restaurante não foi informado"));
			}
			
			/** Verifica se o Nome do Restaurante foi informado na requisição.**/
			if (restaurante.getNome() == null) {
				throw new 	EntidadeNaoEncontradaException(
						 String.format("O Nome do Restaurante não foi informado"));
			}
			
			/** Verifica se a Taxa de Frete do Restaurante foi informada na requisição.**/
			if (restaurante.getTaxaFrete() == null) {
				throw new 	EntidadeNaoEncontradaException(
						 String.format("O Taxa de Frete do Restaurante não foi informada."));
			}
			
			/** Verifica se a cozinha veio na requisição.**/
			if (restaurante.getCozinha() == null) {
				 throw new 	EntidadeNaoEncontradaException(
						 String.format("Não existe um cadastro de Cozinha na requisição deste Restaurante"));
			}
			
			/** Verifica se a Forma de Pagamento veio na requisição. **/
			if (restaurante.getFormasPagamento() == null) {
				 throw new 	EntidadeNaoEncontradaException(
						 String.format("Não existe um cadastro de FormaPagamento na requisição deste Restaurante"));
			}
			
			/** Busca Restaurante no banco. **/
			Optional<Restaurante> restauranteAtual = restauranteRepository.findById(restauranteId);
			
			/** Armazena propriedades do corpo da requisição. **/
			Long cozinhaIdBody = restaurante.getCozinha().getId();
			Long formaPagamentoIdBody = restaurante.getFormasPagamento().getId();
			
			/** Busca Cozinha e FormaPagamento que vieram no corpo da requisição. **/
			Optional<Cozinha> cozinhaBody = cozinhaRepository.findById(cozinhaIdBody);
			Optional<FormaPagamento> formaPagamentoBody = formaPagamentoRepository.findById(formaPagamentoIdBody);
			
			/**
			 *  Testa se Restaurante, Cozinha e FormaPagamento do corpo da requisição são nulos
			 *  e faz o tratamento específico para cada caso.
			 **/	
			if (cozinhaBody.isEmpty()) {
			 throw new 	EntidadeNaoEncontradaException(
					 String.format("Não existe um cadastro de Cozinha com código %d", cozinhaIdBody));
			}
			
			if (formaPagamentoBody.isEmpty()) {
				 throw new 	EntidadeNaoEncontradaException(
						 String.format("Não existe um cadastro de FormaPagamento com código %d", formaPagamentoIdBody));
				}
			
			/**
			 * Testa se Restaurante que veio do banco existe, e se existir, copia as propriedades do Restaurante
			 * que veio no corpo da requisição para o Restaurante Atualizado e faz o salvamento em banco e retorno
			 * apropriado da requisição com 200 Ok.
			 **/
			if (restauranteAtual.isPresent()) {
				BeanUtils.copyProperties(restaurante, restauranteAtual.get(), "restauranteid");
				cadastroRestaurante.salvar(restauranteAtual.get());
				return ResponseEntity.ok(restauranteAtual.get());
			}
			
		}catch (EntidadeNaoEncontradaException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
	
		/** Retorna 404 Not Found no caso de não existir um Restaurante no 
		 * corpo da requisição**/
		return ResponseEntity.notFound().build();
				
	}
	
	@DeleteMapping("/{restauranteId}")
	public ResponseEntity<?> remover(@PathVariable Long restauranteId) {
		
		Optional<Restaurante> restaurante = restauranteRepository.findById(restauranteId);
		
		try {
			
			if (restaurante.isEmpty()) {
				
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
				
			}
						
			cadastroRestaurante.excluir(restauranteId);
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
	
	@PatchMapping("/{restauranteId}")
	public ResponseEntity<?> atualizarParcial(@PathVariable Long restauranteId,
			@RequestBody Map<String, Object> campos) {
		
		Optional<Restaurante> restauranteAtual = restauranteRepository.findById(restauranteId);
		
		if(restauranteAtual.isEmpty()) {
			
			return ResponseEntity.notFound().build();
			
		}
		
		merge(campos, restauranteAtual.get());
		
		//Chamada ao método PUT
		return atualizar(restauranteId, restauranteAtual.get());
	}

	
	private void merge(Map<String, Object> dadosOrigem, Restaurante restauranteDestino) {
		
		//Esta classe mapeia tipos de dados de uma origem para uma entidade destino.
		ObjectMapper objectMapper = new ObjectMapper();
		
		Restaurante restauranteOrigem = objectMapper.convertValue(dadosOrigem, Restaurante.class);
		
		//Copia o Mapa para o objeto Restaurante
		dadosOrigem.forEach((nomePropriedade, valorPropriedade) -> {
			
			Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
			field.setAccessible(true);
			
			Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);
			
			//System.out.println(nomePropriedade + " = " + valorPropriedade + " = " + novoValor);
			
			ReflectionUtils.setField(field, restauranteDestino, novoValor);
			
		});
		
	}
}
