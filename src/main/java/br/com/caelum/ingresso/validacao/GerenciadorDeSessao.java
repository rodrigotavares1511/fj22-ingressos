package br.com.caelum.ingresso.validacao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.com.caelum.ingresso.model.Sessao;

public class GerenciadorDeSessao {
	private List<Sessao> sessoesDaSala;
	
	public GerenciadorDeSessao(List<Sessao> sessoesDaSala){
		this.sessoesDaSala = sessoesDaSala;
	}
	
	private boolean horarioIsValido(Sessao sessaoExistente, Sessao sessaoAtual){

		LocalDate hoje = LocalDate.now();
		LocalDateTime horarioSessao = sessaoExistente.getHorario().atDate(hoje);
		LocalDateTime horarioAtual = sessaoAtual.getHorario().atDate(hoje); 
		
		boolean ehAntes = sessaoAtual.getHorario().isBefore(sessaoExistente.getHorario());
		
		if(ehAntes){
			return sessaoAtual.getHorarioTermino()
					.isBefore(horarioSessao);
		}else{
			return sessaoExistente.getHorarioTermino()
					.isBefore(horarioAtual);			
		}
	}
	
	public boolean cabe(Sessao sessaoAtual){
		return sessoesDaSala
				.stream()
				.map(sessaoExistente -> horarioIsValido(sessaoExistente, sessaoAtual))
				.reduce(Boolean::logicalAnd)
				.orElse(true);
	}

}
