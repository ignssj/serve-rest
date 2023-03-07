package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.StandardException;

@Data
@ToString(includeFieldNames = true)
public class Produto {
    private String nome;
    private int preco;
    private String descricao;
    private int quantidade;
    @JsonIgnore
    private String _id;
}
