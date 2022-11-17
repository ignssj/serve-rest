package template;

import io.restassured.response.Response;
import models.Produto;

import static io.restassured.RestAssured.given;

public class TemplateProdutos extends TemplateGeral {

    public static Response post(String endpoint, Object obj,String token){
        return
                given()
                        .header("authorization",token)
                        .body(obj)
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

    public static Response put(String endpoint,Object obj,String token){
        return
                given()
                        .header("authorization",token)
                        .body(obj)
                        .when()
                        .put(endpoint)
                        .then()
                        .extract()
                        .response();
    }

    public static Produto getProduct(String endpoint){
        return
                given()
                .when()
                .get(endpoint)
                .then()
                        .statusCode(200)
                .extract()
                .body().jsonPath().getObject("$",Produto.class);
    }


}
