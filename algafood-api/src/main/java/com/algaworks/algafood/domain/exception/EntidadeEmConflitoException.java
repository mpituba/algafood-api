package com.algaworks.algafood.domain.exception;

public class EntidadeEmConflitoException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		
		public EntidadeEmConflitoException(String mensagem) {
			super(mensagem);
		}
}
