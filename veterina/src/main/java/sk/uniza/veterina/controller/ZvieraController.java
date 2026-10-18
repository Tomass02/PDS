package sk.uniza.veterina.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.uniza.veterina.repository.MajitelRepository;
import sk.uniza.veterina.repository.ZvieraRepository;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping("/zvierata")
public class ZvieraController {
    private final ZvieraRepository repo;
    private final MajitelRepository majitelia;
    public ZvieraController(ZvieraRepository repo, MajitelRepository majitelia) { this.repo = repo; this.majitelia = majitelia; }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "zviera/list"; }
    @GetMapping("/new") public String createForm(Model m) { m.addAttribute("majitelia", majitelia.options()); return "zviera/form"; }
    @PostMapping public String create(@RequestParam Long idMajitel, @RequestParam String meno, @RequestParam String druh,
                                      @RequestParam String datumNarodenia, @RequestParam(required=false) String plemeno,
                                      @RequestParam(required=false) MultipartFile fotografia) throws IOException {
        repo.insert(idMajitel, meno, druh, LocalDate.parse(datumNarodenia), plemeno, bytes(fotografia)); return "redirect:/zvierata";
    }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) { m.addAttribute("item", repo.findById(id)); m.addAttribute("majitelia", majitelia.options()); return "zviera/form"; }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam Long idMajitel, @RequestParam String meno,
                                               @RequestParam String druh, @RequestParam String datumNarodenia,
                                               @RequestParam(required=false) String plemeno,
                                               @RequestParam(required=false) MultipartFile fotografia) throws IOException {
        repo.update(id, idMajitel, meno, druh, LocalDate.parse(datumNarodenia), plemeno, bytes(fotografia)); return "redirect:/zvierata";
    }
    private byte[] bytes(MultipartFile f) throws IOException { return f == null || f.isEmpty() ? null : f.getBytes(); }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try { repo.delete(id); } catch (DataAccessException e) { ra.addFlashAttribute("error", "Zviera nie je mozne zmazat, pretoze na neho odkazuje zdravotna karta."); }
        return "redirect:/zvierata";
    }
}
