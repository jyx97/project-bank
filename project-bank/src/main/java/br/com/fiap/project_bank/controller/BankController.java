package br.com.fiap.project_bank.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/account")
public class BankController {
private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private PersonalAccountRepository repository;

    //todas as contas
    @GetMapping
    public List<PersonalAccount> index(){
        return repository.findAll();
    }

    // conta por ID
    @GetMapping("id/{id}")
    public ResponseEntity <PersonalAccount> getAccountById(@PathVariable Long id){
        log.info("Buscando conta com o ID: " + id);
        return getAccount(id);
    }

    private ResponseEntity<PersonalAccount> getAccount(Long id) {
        return repository.findById(id)
                .map(account -> ResponseEntity.ok(account))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
   
    // conta por CPF
    @GetMapping("cpf/{cpf}")
    public ResponseEntity <PersonalAccount> getAccountByCpf(@PathVariable String cpf){
        log.info("Buscando conta com o CPF: " + cpf);
        PersonalAccount account = repository.findByCpf(cpf);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //criar nova conta
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<PersonalAccount> create(@RequestBody PersonalAccount account){
        log.info("Criando conta para o titular: " + account.getName());
        account.setOpenDate(LocalDate.now());
        repository.save(account);
        return ResponseEntity.status(201).body(account);
    
    }

    // altera status da conta pra inativa
    @PutMapping("/status/{id}")
    public ResponseEntity<PersonalAccount> closeAccount(@PathVariable Long id) {
        log.info("Encerrando conta " + id);
        PersonalAccount personalAccount = repository.findById(id).orElse(null);

        if (personalAccount == null) {
            return ResponseEntity.notFound().build();
        }
        personalAccount.getStatus().setActive(false);        
        repository.save(personalAccount);
        return ResponseEntity.ok(personalAccount);
    }

    //faz deposito na conta
    @PutMapping("/deposit")
    public ResponseEntity<PersonalAccount> deposit(@RequestParam Long id, @RequestParam Double valueDeposit){
        log.info("Depositando R$ " + valueDeposit + " na conta " + id);
        PersonalAccount account = repository.findById(id).orElse(null);

        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        // atualiza o saldo da conta
        account.setSaldoInicial(account.getSaldoInicial() + valueDeposit);

        // salva a conta com o saldo atualizado
        repository.save(account);

        // retorna a conta com os dados atualizados
        return ResponseEntity.ok(account);
    }

    //faz saque da conta
    @PutMapping("/saque")
    public ResponseEntity<PersonalAccount> saque(@RequestParam Long id, @RequestParam Double valorSaque) {
        log.info("Sacando R$  " + valorSaque + " da conta: " + id);
    
        // Verifica se a conta existe
    PersonalAccount account = repository.findById(id)
        .orElse(null);
    if (account == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    //verificação valor do saque
    if (valorSaque <= 0 || account.getSaldoInicial() <= valorSaque) {
        return ResponseEntity.badRequest().build();
        }

    //realização do saque
    account.setSaldoInicial(account.getSaldoInicial() - valorSaque);
    repository.save(account);  // Atualiza a conta no banco de dados
    return ResponseEntity.ok(account);
    }

    //fazer pix
     @PutMapping("/pix")
     public ResponseEntity<String> pix(@RequestParam long senderId, @RequestParam Long receiverId, @RequestParam Double valuePix){
        log.info("Transferindo R$ " + valuePix + " da conta " + senderId + " para " + receiverId);
        
        
        // Verifica se as contas existem
        PersonalAccount senderPersonalAccount = getPersonalAccount(senderId).orElse(null);
        PersonalAccount receiverPersonalAccount = getPersonalAccount(receiverId).orElse(null);

        if (senderPersonalAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conta remetente não encontrada.");
        }
        if (receiverPersonalAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conta destinatária não encontrada.");
        }
    
        if (valuePix == null || valuePix <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Valor da transferência inválido.");
        }
    
        if (senderPersonalAccount.getSaldoInicial() < valuePix) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Saldo insuficiente na conta remetente.");
        }
    
        // Realiza a transferência
        senderPersonalAccount.setSaldoInicial(senderPersonalAccount.getSaldoInicial() - valuePix);
        receiverPersonalAccount.setSaldoInicial(receiverPersonalAccount.getSaldoInicial() + valuePix);
    
        repository.save(senderPersonalAccount);
        repository.save(receiverPersonalAccount);
    
        return ResponseEntity.ok("Transferência realizada com sucesso.");
    }
    
    // Método para buscar conta usando Optional
    private Optional<PersonalAccount> getPersonalAccount(Long id) {
        return repository.findById(id);
    }
}
