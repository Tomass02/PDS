package sk.uniza.veterina.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.uniza.veterina.repository.ZdravotnaKartaRepository;
import sk.uniza.veterina.repository.ZvieraRepository;

import java.time.LocalDate;

@Controller
@RequestMapping("/karty")
public class ZdravotnaKartaController {
    private final ZdravotnaKartaRepository repo;
    private final ZvieraRepository zvierata;
    public ZdravotnaKartaController(ZdravotnaKartaRepository repo, ZvieraRepository zvierata) { this.repo = repo; this.zvierata = zvierata; }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "karta/list"; }
    @GetMapping("/new") public String createForm(Model m) { m.addAttribute("zvierata", zvierata.options()); return "karta/form"; }
    @PostMapping public String create(@RequestParam Long idZviera, @RequestParam String datumZalozenia, @RequestParam(required=false) String poznamka) { repo.insert(idZviera, LocalDate.parse(datumZalozenia), poznamka); return "redirect:/karty"; }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) { m.addAttribute("item", repo.findById(id)); m.addAttribute("zvierata", zvierata.options()); return "karta/form"; }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam Long idZviera, @RequestParam String datumZalozenia, @RequestParam(required=false) String poznamka) { repo.update(id, idZviera, LocalDate.parse(datumZalozenia), poznamka); return "redirect:/karty"; }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try { repo.delete(id); } catch (DataAccessException e) { ra.addFlashAttribute("error", "Zdravotnu kartu nie je mozne zmazat, pretoze obsahuje vysetrenia."); }
        return "redirect:/karty";
    }
}
