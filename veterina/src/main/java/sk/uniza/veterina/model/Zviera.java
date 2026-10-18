package sk.uniza.veterina.model;

import java.time.LocalDate;

public record Zviera(Long idZviera, Long idMajitel, String majitel, String meno, String druh,
                     LocalDate datumNarodenia, String plemeno, boolean maFotografiu) {}
