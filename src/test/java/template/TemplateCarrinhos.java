package template;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import models.Carrinho;
import models.ProdutosDeCarrinho;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
@Feature("Testes automatizados do endpoint Carrinhos")
@Story("Sendo um usuario de uma loja com cadastro já realizado\n" +
        "Gostaria de poder me autenticar e cadastrar produtos em um carrinho \n" +
        "Para poder comprar meus produtos")
public class TemplateCarrinhos extends TemplateGeral{

    public static Response post(String endpoint, Carrinho carrinho, String token){
        return
                given()
                        .header("authorization",token)
                        .body(carrinho)
                        .when()
                        .post(endpoint)
                        .then()
                        .extract()
                        .response();
    }

    public static Response delete(String endpoint, String token){
        return
                given()
                        .header("authorization",token)
                        .when()
                        .delete(endpoint)
                        .then()
                        .extract()
                        .response();
    }

    public static Response put(String endpoint){
        return
                given()
                        .when()
                        .put(endpoint)
                        .then()
                        .extract()
                        .response();
    }
}
