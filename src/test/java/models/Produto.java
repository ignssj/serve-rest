package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {
    private String nome;
    private int preco;
    private String descricao;
    private int quantidade;
    @JsonIgnore
    private String _id;
}
