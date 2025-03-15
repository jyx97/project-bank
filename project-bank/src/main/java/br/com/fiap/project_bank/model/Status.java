package br.com.fiap.project_bank.model;

public class Status {
    private boolean active;

    public Status(){
        this.active = true;
    }
    public Status(boolean active) {
        this.active = active;
    }

    public String getDescricao() {
        return active ? "ATIVA" : "INATIVA";
    }

}
