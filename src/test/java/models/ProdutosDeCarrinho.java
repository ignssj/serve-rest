package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProdutosDeCarrinho {
    private String idProduto;
    private int quantidade;
}
