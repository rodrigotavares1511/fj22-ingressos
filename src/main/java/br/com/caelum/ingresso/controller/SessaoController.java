package br.com.caelum.ingresso.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.caelum.ingresso.dao.FilmeDao;
import br.com.caelum.ingresso.dao.SalaDao;
import br.com.caelum.ingresso.dao.SessaoDao;
import br.com.caelum.ingresso.model.Carrinho;
import br.com.caelum.ingresso.model.ImagemCapa;
import br.com.caelum.ingresso.model.Sessao;
import br.com.caelum.ingresso.model.TipoDeIngresso;
import br.com.caelum.ingresso.model.form.SessaoForm;
import br.com.caelum.ingresso.rest.ImdbClient;
import br.com.caelum.ingresso.validacao.GerenciadorDeSessao;

@Controller
public class SessaoController {
	@Autowired
	private FilmeDao filmeDao;

	@Autowired
	private SessaoDao sessaoDao;

	@Autowired
	private SalaDao salaDao;

	@Autowired
	private ImdbClient client;

	@Autowired
	private Carrinho carrinho;

	@GetMapping("/admin/sessao")
	public ModelAndView form(Integer salaId, SessaoForm form) {
		form.setSalaId(salaId);
		ModelAndView view = new ModelAndView("sessao/sessao");
		view.addObject("filmes", filmeDao.findAll());
		view.addObject("sala", salaDao.findOne(salaId));
		view.addObject("form", form);
		return view;
	}

	@PostMapping("/admin/sessao")
	@Transactional
	public ModelAndView salva(@Valid SessaoForm form, BindingResult br) {
		if (br.hasErrors()) {
			return form(form.getSalaId(), form);
		}

		ModelAndView view;
		Sessao sessao = form.toSessao(filmeDao, salaDao);
		List<Sessao> sessoes = sessaoDao.buscaSessoesDaSala(sessao.getSala());
		GerenciadorDeSessao vs = new GerenciadorDeSessao(sessoes);

		if (vs.cabe(sessao)) {
			sessaoDao.save(sessao);
			view = new ModelAndView("redirect:/admin/sala/" + form.getSalaId() + "/sessoes");
		} else {
			view = new ModelAndView("sessao/sessao");
			view.addObject("Error", "horário da sessão está em conflito com outra sessao já cadastrada.");
		}

		return view;
	}

	@GetMapping("/sessao/{id}/lugares")
	public ModelAndView lugaresNaSessao(@PathVariable("id") Integer sessaoId) {
		ModelAndView modelAndView = new ModelAndView("sessao/lugares");
		Sessao sessao = sessaoDao.findOne(sessaoId);
		Optional<ImagemCapa> imagemCapa = client.request(sessao.getFilme(), ImagemCapa.class);
		modelAndView.addObject("sessao", sessao);
		modelAndView.addObject("carrinho", carrinho);
		modelAndView.addObject("imagemCapa", imagemCapa.orElse(new ImagemCapa()));
		modelAndView.addObject("tiposDeIngressos", TipoDeIngresso.values());
		return modelAndView;
	}

}
