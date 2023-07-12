package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pessoa {
    @Id
    private Long id;
    private String nome;
    private LocalDate data_nascimento;
    private String email;
    private String Telefone;

    @OneToOne(mappedBy = "endereco")
    private Endereco endereco;

    @ManyToOne
    @JoinColumn(name = "id_cargo")
    private Cargo cargo;

}
