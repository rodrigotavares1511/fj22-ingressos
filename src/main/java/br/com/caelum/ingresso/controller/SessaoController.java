package br.com.caelum.ingresso.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.caelum.ingresso.dao.FilmeDao;
import br.com.caelum.ingresso.dao.SalaDao;
import br.com.caelum.ingresso.dao.SessaoDao;
import br.com.caelum.ingresso.model.Sessao;
import br.com.caelum.ingresso.model.form.SessaoForm;
import br.com.caelum.ingresso.validacao.GerenciadorDeSessao;

@Controller
public class SessaoController {
	@Autowired
	private FilmeDao filmeDao;

	@Autowired
	private SessaoDao sessaoDao;

	@Autowired
	private SalaDao salaDao;

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
}
