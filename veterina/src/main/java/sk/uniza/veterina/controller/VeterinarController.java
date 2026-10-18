package sk.uniza.veterina.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.uniza.veterina.repository.OsobaRepository;
import sk.uniza.veterina.repository.VeterinarRepository;

@Controller
@RequestMapping("/veterinari")
public class VeterinarController {
    private final VeterinarRepository repo;
    private final OsobaRepository osoby;
    public VeterinarController(VeterinarRepository repo, OsobaRepository osoby) { this.repo = repo; this.osoby = osoby; }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "veterinar/list"; }
    @GetMapping("/new") public String createForm(Model m) { m.addAttribute("osoby", osoby.options()); return "veterinar/form"; }
    @PostMapping public String create(@RequestParam Long idOsoba, @RequestParam String specializacia) { repo.insert(idOsoba, specializacia); return "redirect:/veterinari"; }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) { m.addAttribute("item", repo.findById(id)); m.addAttribute("osoby", osoby.options()); return "veterinar/form"; }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam Long idOsoba, @RequestParam String specializacia) { repo.update(id, idOsoba, specializacia); return "redirect:/veterinari"; }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try { repo.delete(id); } catch (DataAccessException e) { ra.addFlashAttribute("error", "Veterinara nie je mozne zmazat, pretoze na neho odkazuju vysetrenia."); }
        return "redirect:/veterinari";
    }
}
