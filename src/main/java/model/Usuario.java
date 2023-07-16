package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Usuario implements InterfacePersist {
    @Id
    @Column(name = "id",columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String login;
    private String senha;
    @Column
    private Integer tipo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pessoa")
    private Pessoa pessoa;

    @Transient
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.fromValor(tipo);
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipo = tipoUsuario.getValor();
    }

    public String getNomeUsuarioETipo() {
        return this.pessoa.getNome() + " → ( " + this.getTipoUsuario().getDescricao() + " ) ";
    }
}
