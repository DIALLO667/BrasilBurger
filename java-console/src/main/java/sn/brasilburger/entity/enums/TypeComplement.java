package sn.brasilburger.entity.enums;

public enum TypeComplement {
    BOISSON("boisson"),
    FRITES("frites");
    
    private final String value;
    
    TypeComplement(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static TypeComplement fromString(String text) {
        for (TypeComplement type : TypeComplement.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type inconnu: " + text);
    }
}