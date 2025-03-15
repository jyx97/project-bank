package br.com.fiap.project_bank.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import br.com.fiap.project_bank.model.PersonalAccount;
import br.com.fiap.project_bank.repository.PersonalAccountRepository;

@RestController
@RequestMapping("/account")
public class BankController {
private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private PersonalAccountRepository repository;
// Endpoint para retornar uma conta por ID
    @GetMapping("/{id}")
    public ResponseEntity<PersonalAccount> getAccountById(@PathVariable Long id) {
        log.info("Buscando conta com ID: " + id);
        return repository.findById(id)
                .map(account -> ResponseEntity.ok(account))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint para retornar uma conta por CPF
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PersonalAccount> getAccountByCpf(@PathVariable String cpf) {
        log.info("Buscando conta com CPF: " + cpf);
        PersonalAccount account = repository.findByCpf(cpf);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para criar uma conta
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<PersonalAccount> create(@RequestBody PersonalAccount personalAccount) {
        log.info("Cadastrando conta para o titular: " + personalAccount.getName());
        repository.save(personalAccount);
        return ResponseEntity.status(201).body(personalAccount);
    }

    // Endpoint para deletar uma conta por ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) {
        log.info("Deletando conta com ID: " + id);
        repository.findById(id).ifPresent(repository::delete);
    }

    private PersonalAccount getCategory(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }
}
