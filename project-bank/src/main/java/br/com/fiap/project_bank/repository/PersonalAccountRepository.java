package br.com.fiap.project_bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.project_bank.model.PersonalAccount;

public interface PersonalAccountRepository extends JpaRepository<PersonalAccount,Long>{
    PersonalAccount findByCpf(String cpf);
    //insert
    //delete
    //select
}
