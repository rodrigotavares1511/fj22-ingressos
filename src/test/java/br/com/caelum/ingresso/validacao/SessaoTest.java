package br.com.caelum.ingresso.validacao;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

import org.junit.Test;

import br.com.caelum.ingresso.model.Filme;
import br.com.caelum.ingresso.model.Sala;
import br.com.caelum.ingresso.model.Sessao;
import org.junit.Assert;

public class SessaoTest {
	
	@Test
	public void precoDaSessaoDeveSerIgualASomaDosPrecosDaSalaEDoFilme(){
		Sala sala = new Sala("Imax", new BigDecimal("5"));
		Filme filme = new Filme("Matrix", Duration.ofMinutes(120), "Sci-fi", new BigDecimal("10"));
		Sessao sessao = new Sessao(LocalTime.parse("10:00:00"), filme, sala);
		BigDecimal soma = sala.getPreco().add(filme.getPreco());
		
		Assert.assertEquals(soma, sessao.getPreco());
	}

}
