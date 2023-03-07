package builders;

import com.github.javafaker.Faker;
import models.Produto;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class ProdutosBuilder {
    public static Faker faker = new Faker(Locale.ENGLISH);

    public static Produto randomProduct(){
        Produto prod = new Produto();
        int randomNum = ThreadLocalRandom.current().nextInt(10, 30);
        prod.setNome(faker.commerce().productName());
        prod.setDescricao(faker.commerce().material());
        prod.setPreco(randomNum);
        prod.setQuantidade(randomNum*2);
        return prod;
    }

    public static Produto emptyProduct(){
        Produto prod = new Produto();
        prod.setNome("");
        prod.setDescricao("");
        prod.setPreco(-1);
        prod.setQuantidade(-1);
        return prod;
    }
}
