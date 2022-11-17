package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProdutosDeCarrinho {
    private String idProduto;
    private int quantidade;
}
