package controller;

import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import model.Pessoa;

@Named
@Getter
@Setter
public class PessoaSalarioMbean {

    @Getter
    @Setter
    public static class RestricoesBusca {
        boolean nome;
        boolean username;
        boolean cargo;
        boolean estado;
    }

    @Getter
    @Setter
    public static class ParametrosBusca {
        String nome;
        String username;
        Integer cargo;
        Integer estado;
    }

    private Pessoa pessoaSelecionada;

}
