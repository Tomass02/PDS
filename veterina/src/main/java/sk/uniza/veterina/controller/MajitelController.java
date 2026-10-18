package sk.uniza.veterina.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.uniza.veterina.repository.MajitelRepository;
import sk.uniza.veterina.repository.OsobaRepository;

@Controller
@RequestMapping("/majitelia")
public class MajitelController {
    private final MajitelRepository repo;
    private final OsobaRepository osoby;
    public MajitelController(MajitelRepository repo, OsobaRepository osoby) { this.repo = repo; this.osoby = osoby; }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "majitel/list"; }
    @GetMapping("/new") public String createForm(Model m) { m.addAttribute("osoby", osoby.options()); return "majitel/form"; }
    @PostMapping public String create(@RequestParam Long idOsoba, @RequestParam String ulica, @RequestParam String mesto,
                                      @RequestParam String psc, @RequestParam String stat) {
        repo.insert(idOsoba, ulica, mesto, psc, stat); return "redirect:/majitelia";
    }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) {
        m.addAttribute("item", repo.findById(id)); m.addAttribute("osoby", osoby.options()); return "majitel/form";
    }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam Long idOsoba, @RequestParam String ulica,
                                               @RequestParam String mesto, @RequestParam String psc, @RequestParam String stat) {
        repo.update(id, idOsoba, ulica, mesto, psc, stat); return "redirect:/majitelia";
    }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try { repo.delete(id); } catch (DataAccessException e) { ra.addFlashAttribute("error", "Majitela nie je mozne zmazat, pretoze na neho odkazuju ine zaznamy."); }
        return "redirect:/majitelia";
    }
}
