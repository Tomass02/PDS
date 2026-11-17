package sk.uniza.veterina.model;

public record VeterinarnyLiek(
        String id,
        String name,
        String pharmaceuticalForm,
        String strength,
        String activeSubstance,
        String status
) {}
