package sk.uniza.veterina.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Faktura(Long idFaktura, Long idVysetrenie, String vysetrenie,
                      BigDecimal sumaCelkom, LocalDate datumVystavenia,
                      LocalDate datumSplatnosti, String stavUhrady, boolean maScan) {}
