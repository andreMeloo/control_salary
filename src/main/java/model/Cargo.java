package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cargo {

    @Id
    private Long id;
    private String nome;
    private Double salario;

    @OneToMany(mappedBy = "pessoa")
    private List<Pessoa> pessoas;
}
