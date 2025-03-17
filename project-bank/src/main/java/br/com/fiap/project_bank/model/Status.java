package br.com.fiap.project_bank.model;

public class Status {
    private boolean active;

    public Status() {
        this.active = true; // Status padrão é ATIVO
    }

    public Status(boolean active) {
        this.active = active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescricao() {
        return active ? "ATIVA" : "INATIVA";
    }

    public boolean isActive() {
        return active;
    }
}
