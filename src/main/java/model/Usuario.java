package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_pessoa")
    private Pessoa pessoa;
    private String login;
    private String senha;
    @Column
    private Integer tipo;

    @Transient
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.fromValor(tipo);
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipo = tipoUsuario.getValor();
    }
}
