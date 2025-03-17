package br.com.fiap.project_bank.model;

import java.time.LocalDate;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Entity

@Data// Gera automaticamente getters, setters, toString, equals, e hashCode via Lombok
public class PersonalAccount {
    public static final Status ACTIVE = null;
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;
    private String agency;
    
    @NotBlank(message = "O nome do titular é obrigatório") 
    private String name;

    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos")
    private String cpf;

    @NotNull(message = "A data de abertura é obrigatória")
    @PastOrPresent(message = "A data de abertura não pode ser no futuro")
    private LocalDate openDate;

    @NotNull(message = "O saldo inicial é obrigatório")
    @PositiveOrZero(message = "O saldo inicial não pode ser negativo")
    private Double saldoInicial;
    
    @NotNull(message = "O tipo da conta é obrigatório")
    @Pattern(regexp = "^(corrente|poupanca|salario)$", message = "Tipo de conta inválido. Deve ser 'corrente', 'poupanca' ou 'salario'.")
    private String type;
    
    @Embedded
    private Status status; 
    public PersonalAccount() {
        this.status = new Status(true); // Conta começa ativa por padrão
    }

}

