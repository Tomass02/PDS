package sk.uniza.veterina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sk.uniza.veterina.repository.LiekRepository;
import sk.uniza.veterina.repository.PredpisRepository;
import sk.uniza.veterina.repository.VysetrenieRepository;

import java.time.LocalDate;

@Controller
@RequestMapping("/predpisy")
public class PredpisController {
    private final PredpisRepository repo;
    private final VysetrenieRepository vysetrenia;
    private final LiekRepository lieky;
    public PredpisController(PredpisRepository repo, VysetrenieRepository vysetrenia, LiekRepository lieky) {
        this.repo = repo;
        this.vysetrenia = vysetrenia;
        this.lieky = lieky;
    }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "predpis/list"; }
    @GetMapping("/new") public String createForm(Model m) { fillOptions(m); return "predpis/form"; }
    @PostMapping public String create(@RequestParam Long idVysetrenie, @RequestParam Long idLiek,
                                      @RequestParam(required=false) String datumOd, @RequestParam(required=false) String datumDo,
                                      @RequestParam(required=false) String poznamka) {
        repo.insert(idVysetrenie, idLiek, dateOrNull(datumOd), dateOrNull(datumDo), poznamka); return "redirect:/predpisy";
    }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) { m.addAttribute("item", repo.findById(id)); fillOptions(m); return "predpis/form"; }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam Long idVysetrenie, @RequestParam Long idLiek,
                                               @RequestParam(required=false) String datumOd, @RequestParam(required=false) String datumDo,
                                               @RequestParam(required=false) String poznamka) {
        repo.update(id, idVysetrenie, idLiek, dateOrNull(datumOd), dateOrNull(datumDo), poznamka); return "redirect:/predpisy";
    }
    private void fillOptions(Model model) {
        model.addAttribute("vysetrenia", vysetrenia.options());
        model.addAttribute("lieky", lieky.options());
    }
    private LocalDate dateOrNull(String value) { return value == null || value.isBlank() ? null : LocalDate.parse(value); }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id) { repo.delete(id); return "redirect:/predpisy"; }
}
