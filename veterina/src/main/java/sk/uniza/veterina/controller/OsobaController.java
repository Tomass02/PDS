package sk.uniza.veterina.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.uniza.veterina.repository.OsobaRepository;

@Controller
@RequestMapping("/osoby")
public class OsobaController {
    private final OsobaRepository repo;
    public OsobaController(OsobaRepository repo) { this.repo = repo; }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "osoba/list"; }
    @GetMapping("/new") public String createForm() { return "osoba/form"; }
    @PostMapping public String create(@RequestParam String meno, @RequestParam String priezvisko,
                                      @RequestParam(required=false) String telefon, @RequestParam(required=false) String email) {
        repo.insert(meno, priezvisko, telefon, email); return "redirect:/osoby";
    }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) { m.addAttribute("item", repo.findById(id)); return "osoba/form"; }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam String meno, @RequestParam String priezvisko,
                                               @RequestParam(required=false) String telefon, @RequestParam(required=false) String email) {
        repo.update(id, meno, priezvisko, telefon, email); return "redirect:/osoby";
    }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try { repo.delete(id); } catch (DataAccessException e) { ra.addFlashAttribute("error", "Osobu nie je mozne zmazat, pretoze na nu odkazuju ine zaznamy."); }
        return "redirect:/osoby";
    }
}
