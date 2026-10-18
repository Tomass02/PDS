package sk.uniza.veterina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sk.uniza.veterina.repository.FakturaRepository;
import sk.uniza.veterina.repository.PlatbaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/platby")
public class PlatbaController {
    private final PlatbaRepository repo;
    private final FakturaRepository faktury;
    public PlatbaController(PlatbaRepository repo, FakturaRepository faktury) { this.repo = repo; this.faktury = faktury; }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "platba/list"; }
    @GetMapping("/new") public String createForm(Model m) { m.addAttribute("faktury", faktury.options()); return "platba/form"; }
    @PostMapping public String create(@RequestParam Long idFaktura, @RequestParam String datumCasPlatby,
                                      @RequestParam BigDecimal suma, @RequestParam(required=false) String sposobPlatby,
                                      @RequestParam(required=false) String variabilnySymbol) {
        repo.insert(idFaktura, LocalDateTime.parse(datumCasPlatby), suma, sposobPlatby, variabilnySymbol); return "redirect:/platby";
    }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) { m.addAttribute("item", repo.findById(id)); m.addAttribute("faktury", faktury.options()); return "platba/form"; }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam Long idFaktura, @RequestParam String datumCasPlatby,
                                               @RequestParam BigDecimal suma, @RequestParam(required=false) String sposobPlatby,
                                               @RequestParam(required=false) String variabilnySymbol) {
        repo.update(id, idFaktura, LocalDateTime.parse(datumCasPlatby), suma, sposobPlatby, variabilnySymbol); return "redirect:/platby";
    }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id) { repo.delete(id); return "redirect:/platby"; }
}
