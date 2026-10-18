package sk.uniza.veterina.model;

import java.math.BigDecimal;

public record PolozkaFaktury(Long idPolozkaFaktury, Long idFaktura, String faktura,
                            String nazov, BigDecimal mnozstvo, BigDecimal jednotkovaCena,
                            BigDecimal sadzbaDph) {}
