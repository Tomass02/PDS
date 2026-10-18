package sk.uniza.veterina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sk.uniza.veterina.repository.FakturaRepository;
import sk.uniza.veterina.repository.PolozkaFakturyRepository;

import java.math.BigDecimal;

@Controller
@RequestMapping("/polozky")
public class PolozkaFakturyController {
    private final PolozkaFakturyRepository repo;
    private final FakturaRepository faktury;
    public PolozkaFakturyController(PolozkaFakturyRepository repo, FakturaRepository faktury) { this.repo = repo; this.faktury = faktury; }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "polozka/list"; }
    @GetMapping("/new") public String createForm(Model m) { m.addAttribute("faktury", faktury.options()); return "polozka/form"; }
    @PostMapping public String create(@RequestParam Long idFaktura, @RequestParam String nazov,
                                      @RequestParam(required=false) BigDecimal mnozstvo,
                                      @RequestParam(required=false) BigDecimal jednotkovaCena,
                                      @RequestParam(required=false) BigDecimal sadzbaDph) {
        repo.insert(idFaktura, nazov, mnozstvo, jednotkovaCena, sadzbaDph); return "redirect:/polozky";
    }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) { m.addAttribute("item", repo.findById(id)); m.addAttribute("faktury", faktury.options()); return "polozka/form"; }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam Long idFaktura, @RequestParam String nazov,
                                               @RequestParam(required=false) BigDecimal mnozstvo,
                                               @RequestParam(required=false) BigDecimal jednotkovaCena,
                                               @RequestParam(required=false) BigDecimal sadzbaDph) {
        repo.update(id, idFaktura, nazov, mnozstvo, jednotkovaCena, sadzbaDph); return "redirect:/polozky";
    }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id) { repo.delete(id); return "redirect:/polozky"; }
}
