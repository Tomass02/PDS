package sk.uniza.veterina.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.uniza.veterina.repository.VeterinarRepository;
import sk.uniza.veterina.repository.VysetrenieRepository;
import sk.uniza.veterina.repository.ZdravotnaKartaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/vysetrenia")
public class VysetrenieController {
    private final VysetrenieRepository repo;
    private final ZdravotnaKartaRepository karty;
    private final VeterinarRepository veterinari;

    public VysetrenieController(VysetrenieRepository repo, ZdravotnaKartaRepository karty, VeterinarRepository veterinari) {
        this.repo = repo; this.karty = karty; this.veterinari = veterinari;
    }

    @GetMapping public String list(Model m) { m.addAttribute("items", repo.findAll()); return "vysetrenie/list"; }
    @GetMapping("/new") public String createForm(Model m) { fillOptions(m); return "vysetrenie/form"; }
    @PostMapping public String create(@RequestParam Long idZdravotnaKarta, @RequestParam Long idVeterinar,
                                      @RequestParam String datumCasVysetrenia, @RequestParam(required=false) String diagnoza,
                                      @RequestParam(required=false) String popisLiecby, @RequestParam BigDecimal teplotaC,
                                      @RequestParam Integer srdecnyTep, @RequestParam BigDecimal hmotnostKg,
                                      @RequestParam String zavaznost) {
        repo.insert(idZdravotnaKarta, idVeterinar, LocalDateTime.parse(datumCasVysetrenia), diagnoza, popisLiecby,
                teplotaC, srdecnyTep, hmotnostKg, zavaznost);
        return "redirect:/vysetrenia";
    }
    @GetMapping("/{id}/edit") public String editForm(@PathVariable Long id, Model m) { m.addAttribute("item", repo.findById(id)); fillOptions(m); return "vysetrenie/form"; }
    @PostMapping("/{id}") public String update(@PathVariable Long id, @RequestParam Long idZdravotnaKarta,
                                               @RequestParam Long idVeterinar, @RequestParam String datumCasVysetrenia,
                                               @RequestParam(required=false) String diagnoza,
                                               @RequestParam(required=false) String popisLiecby,
                                               @RequestParam BigDecimal teplotaC, @RequestParam Integer srdecnyTep,
                                               @RequestParam BigDecimal hmotnostKg, @RequestParam String zavaznost) {
        repo.update(id, idZdravotnaKarta, idVeterinar, LocalDateTime.parse(datumCasVysetrenia), diagnoza, popisLiecby,
                teplotaC, srdecnyTep, hmotnostKg, zavaznost);
        return "redirect:/vysetrenia";
    }
    private void fillOptions(Model m) { m.addAttribute("karty", karty.options()); m.addAttribute("veterinari", veterinari.options()); }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try { repo.delete(id); } catch (DataAccessException e) { ra.addFlashAttribute("error", "Vysetrenie nie je mozne zmazat, pretoze na neho odkazuju faktury alebo predpisy."); }
        return "redirect:/vysetrenia";
    }
}
