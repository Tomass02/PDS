package sk.uniza.veterina.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sk.uniza.veterina.repository.ReportRepository;

@Controller
public class ReportController {
    private final ReportRepository repo;
    public ReportController(ReportRepository repo) { this.repo = repo; }

    @GetMapping("/report/object")
    public String objectReport(Model model) {
        model.addAttribute("items", repo.objectReport());
        return "report/object";
    }

    @GetMapping(value = "/report/json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String jsonReport() { return repo.jsonReport(); }
}
