package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
public class Carrinho {
    private ArrayList produtosIncluidos = new ArrayList<ProdutosDeCarrinho>();
    @JsonIgnore
    private String _id;
}
