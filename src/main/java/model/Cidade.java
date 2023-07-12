package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cidade {

    @Id
    private Long id;
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
