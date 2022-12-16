package com.algaworks.algafood.api.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.model.CozinhasXmlWrapper;
import  com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;

@RestController
@RequestMapping("/cozinhas")
public class CozinhaController {
	
	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@GetMapping
	public List<Cozinha> listar() {
		return cozinhaRepository.listar();
	}
	
	//Retorna Wrapper apenas para XML, tem modelo próprio
	@GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
	public CozinhasXmlWrapper listarXml() {
		return new CozinhasXmlWrapper(cozinhaRepository.listar());
	}
	
	@GetMapping("/{cozinhaId}")
	public ResponseEntity<Cozinha> buscar(@PathVariable Long cozinhaId) {
		Cozinha cozinha = cozinhaRepository.buscar(cozinhaId);
		
		//Retorna a resposta padrão quando há um Id
		if (cozinha != null) {
			return ResponseEntity.ok(cozinha);
		}
		
		//Retorna 404 Not Found quando o Id é nulo no banco
		return ResponseEntity.notFound().build();
	}
	
	//Este mapeamento chama um POST em /cozinhas
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Cozinha adicionar(@RequestBody Cozinha cozinha) {
		return cozinhaRepository.salvar(cozinha);
	}
		
	
	@PutMapping("/{cozinhaId}")
	public ResponseEntity<Cozinha> atualizar(@PathVariable Long cozinhaId,
			@RequestBody Cozinha cozinha) {
		
		/**Cozinha vem do banco por id via @PathVariable
		 * Entidade do Requestbody é copiada a uma entidade buscada em banco
		 * O if verifica se a Entidade no banco é nula. Pois pode não existir.
		 * Cópia pode ser feita propriedade a propriedade ou via BeansUtils
		 * que fará uma cópia da entidade inteira. Do terceiro parâmetro em diante,
		 * são as propriedades que não devem ser copiadas, pois queremos manter o Id,
		 * caso contrário teremos um Id nulo e erro ao tentar atualizar.
		 * Entidade atualizada via BeansUtils e que veio do banco é persistida.
		 * Retorno é enviado ao Builder dos ReponseEntity
		 * O segundo retorno é para o caso da entidade buscada em banco ser nula.
		 */
		
		Cozinha cozinhaAtual = cozinhaRepository.buscar(cozinhaId);
		
		if (cozinhaAtual != null) {
			//cozinhaAtual.setNome(cozinha.getNome());
			BeanUtils.copyProperties(cozinha, cozinhaAtual, "id");
			cozinhaRepository.salvar(cozinhaAtual);
			return ResponseEntity.ok(cozinhaAtual);
		}
		
		return ResponseEntity.notFound().build();
				
	}
	
	@DeleteMapping("/{cozinhaId}")
	public ResponseEntity<Cozinha> remover (@PathVariable Long cozinhaId) {
		
		try {
			Cozinha cozinha = cozinhaRepository.buscar(cozinhaId);
			if (cozinha !=null) {
				cozinhaRepository.remover(cozinha);
		
				return ResponseEntity.noContent().build();
			}
			
			return ResponseEntity.notFound().build();
		}catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
}
