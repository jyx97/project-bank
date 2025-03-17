package br.com.fiap.project_bank.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")

public class DuplaController {
    @GetMapping
    public String Info(){
        return "PROJECT BANK \n\nIntegrantes \n\n-Júlia Soares \n-Sofia Domingues ";
    } 
}


