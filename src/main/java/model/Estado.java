package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Estado {

    @Id
    private Long id;
    private String Nome;

    @Column(columnDefinition = "bpchar(2)")
    private String uf;
}