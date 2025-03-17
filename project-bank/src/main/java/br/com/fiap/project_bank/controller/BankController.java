package br.com.fiap.project_bank.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


    // Endpoint para realizar o depósito
    @PutMapping("/deposit")
    public ResponseEntity<PersonalAccount> deposit(@RequestParam Long accountId, @RequestParam Double valorDeposito) {
        log.info("Realizando depósito de " + valorDeposito + " na conta com ID: " + accountId);

        // Verifica se a conta existe
        PersonalAccount account = repository.findById(accountId)
                .orElse(null);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Valida o valor do depósito
        if (valorDeposito <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Atualiza o saldo da conta
        account.setSaldoinicial(account.getSaldoinicial() + valorDeposito);

        // Salva a conta com o saldo atualizado
        repository.save(account);

        // Retorna a conta com os dados atualizados
        return ResponseEntity.ok(account);
    }


    // Endpoint para realizar o saque
    @PutMapping("/withdraw")
    public ResponseEntity<PersonalAccount> withdraw(@RequestParam Long accountId, @RequestParam Double amount) {
        log.info("Realizando saque de " + amount + " da conta com ID: " + accountId);

        // Verifica se a conta existe
        PersonalAccount account = repository.findById(accountId)
                .orElse(null);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Valida o valor do saque
        if (amount <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Verifica se o saldo é suficiente
        if (account.getSaldoinicial() < amount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Saldo insuficiente
        }

        // Atualiza o saldo da conta
        account.setSaldoinicial(account.getSaldoinicial() - amount);

        // Salva a conta com o saldo atualizado
        repository.save(account);

        // Retorna a conta com os dados atualizados
        return ResponseEntity.ok(account);
    }

}