package sk.uniza.veterina.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.uniza.veterina.repository.FakturaRepository;
import sk.uniza.veterina.repository.VysetrenieRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/faktury")
public class FakturaController {
    private final FakturaRepository repo;
    private final VysetrenieRepository vysetrenia;
    public FakturaController(FakturaRepository repo, VysetrenieRepository vysetrenia) { this.repo = repo; this.vysetrenia = vysetrenia; }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "faktura/list"; }
    @GetMapping("/new") public String createForm(Model m) { m.addAttribute("vysetrenia", vysetrenia.options()); return "faktura/form"; }
    @PostMapping public String create(@RequestParam Long idVysetrenie, @RequestParam BigDecimal sumaCelkom,
                                      @RequestParam String datumVystavenia, @RequestParam String datumSplatnosti,
                                      @RequestParam(required=false) String stavUhrady,
                                      @RequestParam(required=false) MultipartFile scanFaktury) throws IOException {
        repo.insert(idVysetrenie, sumaCelkom, LocalDate.parse(datumVystavenia), LocalDate.parse(datumSplatnosti), stavUhrady, bytes(scanFaktury));
        return "redirect:/faktury";
    }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) { m.addAttribute("item", repo.findById(id)); m.addAttribute("vysetrenia", vysetrenia.options()); return "faktura/form"; }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam Long idVysetrenie,
                                               @RequestParam BigDecimal sumaCelkom, @RequestParam String datumVystavenia,
                                               @RequestParam String datumSplatnosti, @RequestParam(required=false) String stavUhrady,
                                               @RequestParam(required=false) MultipartFile scanFaktury) throws IOException {
        repo.update(id, idVysetrenie, sumaCelkom, LocalDate.parse(datumVystavenia), LocalDate.parse(datumSplatnosti), stavUhrady, bytes(scanFaktury));
        return "redirect:/faktury";
    }
    private byte[] bytes(MultipartFile f) throws IOException { return f == null || f.isEmpty() ? null : f.getBytes(); }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try { repo.delete(id); } catch (DataAccessException e) { ra.addFlashAttribute("error", "Fakturu nie je mozne zmazat, pretoze ma polozky alebo platby."); }
        return "redirect:/faktury";
    }
}
