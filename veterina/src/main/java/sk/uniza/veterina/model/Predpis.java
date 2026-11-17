package sk.uniza.veterina.model;

import java.time.LocalDate;

public record Predpis(Long idPredpis, Long idVysetrenie, String vysetrenie, Long idLiek,
                      String nazovLieku, LocalDate datumOd, LocalDate datumDo, String poznamka) {}
