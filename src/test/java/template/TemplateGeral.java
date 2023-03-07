package template;

import com.github.javafaker.Faker;
import helper.EnvironmentConfig;
import io.qameta.allure.Epic;
import io.qameta.allure.Link;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Carrinho;
import models.Produto;
import models.Usuario;
import org.junit.jupiter.api.BeforeAll;

import java.util.Locale;

import static io.restassured.RestAssured.*;
@Link(name = "Produção",type = "https://compassuol.serverest.dev")
@Link@Link(name = "Local",type = "http://localhost:3000")
@Epic("Análise e testes - API ServeRest")
public class TemplateGeral {
    private static String token;
    private static Produto prod;
    private static Usuario user;
    private static Carrinho carrinho;
    private static Faker faker = new Faker(Locale.ENGLISH);
    @BeforeAll
    public static void setUp() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(EnvironmentConfig.getProperty("url",""))
                .setContentType(ContentType.JSON)
                .build();
    }

    public static Response get(String endpoint){
        return
                given()
                        .when()
                        .get(endpoint)
                        .then()
                        .extract()
                        .response();
    }

    public static Response delete(String endpoint){
        return
                given()
                        .when()
                        .delete(endpoint)
                        .then()
                        .extract()
                        .response();
    }

    public static Response post(String endpoint, Object obj){
        return
                given()
                        .body(obj)
                        .when()
                        .post(endpoint)
                        .then()
                        .extract()
                        .response();
    }

    public static Response put(String endpoint,Object obj){
        return
                given()
                        .body(obj)
                        .when()
                        .put(endpoint)
                        .then()
                        .extract()
                        .response();
    }
}
