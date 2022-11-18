package builders;

import com.github.javafaker.Faker;
import models.Carrinho;
import models.Produto;
import models.ProdutosDeCarrinho;
import template.TemplateProdutos;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static constants.EndpointsPaths.PRODUTOS_ENDPOINT;

public class CarrinhosBuilder {
    public static Faker faker = new Faker(Locale.ENGLISH);

    public static Carrinho retornaUmCarrinho(Produto prod){
        ArrayList<ProdutosDeCarrinho> listaCarrinho = new ArrayList<ProdutosDeCarrinho>();
        ProdutosDeCarrinho produtosDeCarrinho = new ProdutosDeCarrinho(prod.get_id(), 1);
        listaCarrinho.add(produtosDeCarrinho);
        Carrinho carrinho = new Carrinho();
        carrinho.setProdutos(listaCarrinho);
        return carrinho;
    }

    public static Carrinho retornaCarrinhoProdutoInvalido(String idInvalido){
        ArrayList<ProdutosDeCarrinho> listaCarrinho = new ArrayList<ProdutosDeCarrinho>();
        ProdutosDeCarrinho produtosDeCarrinho = new ProdutosDeCarrinho(idInvalido, 22);
        listaCarrinho.add(produtosDeCarrinho);
        Carrinho carrinho = new Carrinho();
        carrinho.setProdutos(listaCarrinho);
        return carrinho;
    }

    public static Carrinho retornaUmCarrinhoInsuficiente(Produto prod){
        ArrayList<ProdutosDeCarrinho> listaCarrinho = new ArrayList<ProdutosDeCarrinho>();
        ProdutosDeCarrinho produtosDeCarrinho = new ProdutosDeCarrinho(prod.get_id(), prod.getQuantidade()+1);
        listaCarrinho.add(produtosDeCarrinho);
        Carrinho carrinho = new Carrinho();
        carrinho.setProdutos(listaCarrinho);
        return carrinho;
    }

    public static Carrinho retornaUmCarrinhoDuplicado(Produto prod){
        ArrayList<ProdutosDeCarrinho> listaCarrinho = new ArrayList<ProdutosDeCarrinho>();
        ProdutosDeCarrinho produtosDeCarrinho = new ProdutosDeCarrinho(prod.get_id(), prod.getQuantidade()+1);
        listaCarrinho.add(produtosDeCarrinho);
        listaCarrinho.add(produtosDeCarrinho);
        Carrinho carrinho = new Carrinho();
        carrinho.setProdutos(listaCarrinho);
        return carrinho;
    }

}
