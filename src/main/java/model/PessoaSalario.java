package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PessoaSalario {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_pessoa")
    private Pessoa pessoa;
    private String nome;
    private Double salario;

}
