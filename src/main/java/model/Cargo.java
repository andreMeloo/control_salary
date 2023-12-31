package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cargo implements InterfacePersist {

    public static final int GERENTE = 5;

    @Id
    @Column(name = "id",columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private BigDecimal salario;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cargo) {
            Cargo cargoCompare = (Cargo) obj;
            if (cargoCompare.getId() == this.id && cargoCompare.getSalario().equals(this.salario)) {
                return true;
            }
        }

        return false;
    }

    public String nomeCargoESalario() {
        return (this.getNome() + " - R$" + this.getSalario().toString() );
    }
}
