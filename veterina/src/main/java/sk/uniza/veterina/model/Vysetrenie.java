package sk.uniza.veterina.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Vysetrenie(Long idVysetrenie, Long idZdravotnaKarta, String zviera,
                         Long idVeterinar, String veterinar, LocalDateTime datumCasVysetrenia,
                         String diagnoza, String popisLiecby, BigDecimal teplotaC,
                         Integer srdecnyTep, BigDecimal hmotnostKg, String zavaznost) {}
