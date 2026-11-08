package sk.uniza.veterina.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestClientException;
import sk.uniza.veterina.model.VeterinarnyLiek;
import sk.uniza.veterina.service.VeterinarnyLiekService;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarne-lieky")
public class VeterinarnyLiekController {
    private final VeterinarnyLiekService service;

    public VeterinarnyLiekController(VeterinarnyLiekService service) {
        this.service = service;
    }

    @GetMapping
    public List<VeterinarnyLiek> search(
            @RequestParam(defaultValue = "") String query) {
        if (query.isBlank() || query.trim().length() < 2) {
            return List.of();
        }
        try {
            return service.search(query.trim(), 20);
        } catch (IllegalStateException | RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "EU databáza veterinárnych liekov nie je dostupná alebo má neplatnú odpoveď.", exception);
        }
    }
}
