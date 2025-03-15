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
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;
    private String agency;
    
    @NotBlank(message = "O nome do titular é obrigatório") 
    private String name;

    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos")
    private String cpf;

    @NotNull(message = "A data de abertura é obrigatória")
    @PastOrPresent(message = "A data de abertura não pode ser no futuro")
    private LocalDate opendate;

    @NotNull(message = "O saldo inicial é obrigatório")
    @PositiveOrZero(message = "O saldo inicial não pode ser negativo")
    private Double saldoinicial;
    
    @NotNull(message = "O tipo da conta é obrigatório")
    @Enumerated(EnumType.STRING)
    private Type type;
    
    @Embedded
    private Status status; 

    public PersonalAccount() {
        this.status = new Status(true); // Conta começa ativa por padrão
    }
}

//A classe `PersonalAccount` representa uma conta bancária e é uma entidade mapeada para o banco de dados usando **JPA**. Ela contém informações como ID, agência, nome do titular, CPF, data de abertura, saldo inicial, tipo da conta e status. A anotação `@Entity` indica que a classe será armazenada no banco, enquanto `@Data` do **Lombok** gera automaticamente **getters, setters, toString, equals e hashCode**. O ID é a chave primária e gerado automaticamente pelo banco com `@GeneratedValue(strategy = GenerationType.IDENTITY)`. O nome do titular é obrigatório (`@NotBlank`), o CPF deve conter exatamente 11 dígitos (`@Pattern(regexp = "\\d{11}")`), a data de abertura não pode ser no futuro (`@PastOrPresent`), o saldo inicial não pode ser negativo (`@PositiveOrZero`), e o tipo da conta (`Type`) deve ser informado (`@NotNull`). O campo `status` é incorporado (`@Embedded`) e, por padrão, a conta inicia ativa, definida no construtor. Essas validações garantem a consistência dos dados e evitam informações inválidas no sistema.