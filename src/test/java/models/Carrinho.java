package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@ToString(includeFieldNames = true)
public class Carrinho {
    private ArrayList produtos = new ArrayList<ProdutosDeCarrinho>();
    @JsonIgnore
    private String _id;
    @JsonIgnore
    private int precoTotal;
    @JsonIgnore
    private int quantidadeTotal;
    @JsonIgnore
    private String idUsuario;
}
