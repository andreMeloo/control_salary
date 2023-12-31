package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Endereco implements InterfacePersist {

    @Id
    @Column(name = "id",columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String Descricao;
    private String cep;
    private String pais;

    @ManyToOne
    @JoinColumn(name = "id_cidade")
    private Cidade cidade;
}
