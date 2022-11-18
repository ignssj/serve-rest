package tests.Usuarios.Delete;

import builders.ProdutosBuilder;
import com.github.javafaker.Faker;
import builders.UsuariosBuilder;
import template.TemplateGeral;
import io.restassured.response.Response;
import models.*;
import org.junit.jupiter.api.Test;
import template.TemplateUsuarios;

import java.util.Locale;

import static constants.EndpointsPaths.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class UsuarioDeleteTest extends TemplateUsuarios {
    private static Faker faker = new Faker(Locale.ENGLISH);
    private static Usuario user;
    private static Produto prod;
    private static String token;

    @Test
    public void deveExcluirRegistro(){
        user = UsuariosBuilder.admUser();

        Response response = post(USUARIOS_ENDPOINT,user);
        assertThat(response.statusCode(),is(201));
        String idExtraido = response.then().extract().path("_id");
        user.set_id(idExtraido);

        response = delete(USUARIOS_ENDPOINT+"/"+user.get_id());
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Registro excluído com sucesso"));
    }

    @Test
    public void deveFalharDeleteUsuarioInexistente(){
        Response response = delete(USUARIOS_ENDPOINT+"/"+faker.internet().password());
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Nenhum registro excluído"));
    }

    @Test
    public void deveFalharDeleteUsuarioComCarrinho(){
        user = UsuariosBuilder.admUser();

        Response response = post(USUARIOS_ENDPOINT,user);
        assertThat(response.statusCode(),is(201));
        String idExtraido = response.then().extract().path("_id");
        user.set_id(idExtraido);

        Login login = Login.of(user.getEmail(),user.getPassword());

        response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(200));
        token = response.then().extract().path("authorization");

        prod = ProdutosBuilder.randomProduct();

        String idProd = given()
                .header("authorization",token)
                .and()
                .body(prod)
                .when()
                .post(PRODUTOS_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .extract().path("_id");
        prod.set_id(idProd);

        given()
                .header("authorization",token)
                .and()
                .body("{\n" +
                        "  \"produtos\": [\n" +
                        "    {\n" +
                        "      \"idProduto\": \""+prod.get_id()+"\",\n" +
                        "      \"quantidade\": 1\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}")
                .when()
                .post(CARRINHOS_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(201);

        response = delete(USUARIOS_ENDPOINT+"/"+user.get_id());
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),equalTo("Não é permitido excluir usuário com carrinho cadastrado"));

        given()
                .header("authorization",token)
                .when()
                .delete(CARRINHOS_ENDPOINT+"/cancelar-compra")
                .then()
                .statusCode(200);

        given()
                .header("authorization",token)
                .when()
                .delete(PRODUTOS_ENDPOINT+"/{id}", prod.get_id())
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("message", equalTo("Registro excluído com sucesso"))
        ;

        response = delete(USUARIOS_ENDPOINT+"/"+user.get_id());
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Registro excluído com sucesso"));
    }
}
