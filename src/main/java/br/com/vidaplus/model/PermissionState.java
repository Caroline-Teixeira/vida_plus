package br.com.vidaplus.model;

public enum PermissionState {
    ACTIVE(1),
    INACTIVE(2),
    READ(4),
    WRITE(8),
    MANAGEMENT(16);

    private final int value;

    // Construtor
    PermissionState(int value) {
        this.value = value;
    }

    // GETTER: permite acessar o valor associado a cada permissão
    public int getValue() {
        return value;
    }

}
