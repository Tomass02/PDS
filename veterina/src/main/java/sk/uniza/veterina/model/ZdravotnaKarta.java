package sk.uniza.veterina.model;

import java.time.LocalDate;

public record ZdravotnaKarta(Long idZdravotnaKarta, Long idZviera, String zviera,
                             LocalDate datumZalozenia, String poznamka) {}
