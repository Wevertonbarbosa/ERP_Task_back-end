package br.com.happydo.model;

public enum UsuarioRole {
    ADMIN("Adm"),
    USER("User");

    private String role;

    UsuarioRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
