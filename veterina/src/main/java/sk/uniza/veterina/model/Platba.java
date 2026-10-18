package sk.uniza.veterina.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Platba(Long idPlatba, Long idFaktura, String faktura, LocalDateTime datumCasPlatby,
                     BigDecimal suma, String sposobPlatby, String variabilnySymbol) {}
