package br.com.vidaplus.model;

public enum Profile {
    PATIENT(1),
    HEALTH_PROFESSIONAL(2),
    ATTENDANT(4),
    ADMIN(8);

    private final int value;

    // Construtor
    Profile(int value) {
        this.value = value;
    }

    // GETTER: permite acessar o valor associado a cada perfil
    public int getValue() {
        return value;
    }

}
