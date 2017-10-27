package br.com.caelum.ingresso.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Sessao {

	@Id
	@GeneratedValue
	private Integer id;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime horario;

	@ManyToOne
	private Filme filme;

	@ManyToOne
	private Sala sala;
	
	@OneToMany(mappedBy = "sessao", fetch = FetchType.EAGER)
	private Set<Ingresso> ingressos = new HashSet<>();

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	private BigDecimal preco;

	public Sessao() {
	}

	public Sessao(LocalTime horario, Filme filme, Sala sala) {
		super();
		this.horario = horario;
		this.filme = filme;
		this.sala = sala;
		this.preco = this.filme.getPreco().add(this.sala.getPreco());
	}

	public LocalDateTime getHorarioTermino() {
		LocalDateTime horario = this.getHorario().atDate(LocalDate.now());

		return horario.plus(filme.getDuracao());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalTime getHorario() {
		return horario;
	}

	public void setHorario(LocalTime horario) {
		this.horario = horario;
	}

	public Filme getFilme() {
		return filme;
	}

	public void setFilme(Filme filme) {
		this.filme = filme;
	}

	public Sala getSala() {
		return sala;
	}

	public void setSala(Sala sala) {
		this.sala = sala;
	}
	
	public Map<String, List<Lugar>> getMapaDeLugares(){
		return sala.getMapaDeLugares();
	}
	
	public boolean isDisponivel(Lugar lugarSelecionado){
		return ingressos
				.stream()
				.map(Ingresso::getLugar)
				.noneMatch(lugar -> lugar.equals(lugarSelecionado));
	}

	public Set<Ingresso> getIngressos() {
		return ingressos;
	}

	public void setIngressos(Set<Ingresso> ingressos) {
		this.ingressos = ingressos;
	}
}
