package tests.Produtos.Delete;

import builders.ProdutosBuilder;
import builders.UsuariosBuilder;
import io.restassured.response.Response;
import models.Login;
import models.Produto;
import models.Usuario;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static constants.EndpointsPaths.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import com.github.javafaker.Faker;
import template.TemplateProdutos;

public class ProdutoDeleteTest extends TemplateProdutos {
    private static String token;
    private static Usuario user;
    private static Produto prod;
    private static Faker faker = new Faker(Locale.ENGLISH);

    @BeforeAll
    public static void LogarUsuarioECadastrarProduto(){
        user = UsuariosBuilder.admUser();
        Response response = post(USUARIOS_ENDPOINT,user);
        String idExtraido = response.then().extract().path("_id");
        assertThat(response.statusCode(),is(201));
        user.set_id(idExtraido);
        Login login = new Login(user.getEmail(),user.getPassword());
        response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Login realizado com sucesso"));
        token = response.then().extract().path("authorization");
    }

    @AfterAll
    public static void DeletaUsuario(){
        delete(USUARIOS_ENDPOINT+"/"+user.get_id()).then().statusCode(200).body("message",equalTo("Registro excluído com sucesso"));
    }

    @Test
    public void deveDeletarProduto(){
        prod = ProdutosBuilder.randomProduct();
        Response response = post(PRODUTOS_ENDPOINT,prod,token);
        assertThat(response.statusCode(),is(201));
        String idExtraido = response.then().extract().path("_id");
        prod.set_id(idExtraido);
        response = delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Registro excluído com sucesso"));
    }

    @Test
    public void deveFalharDeletarIdInexistente(){
        Response response = delete(PRODUTOS_ENDPOINT+"/"+faker.internet().password(),token);
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Nenhum registro excluído"));
    }

    @Test
    public void deveFalharDeletarTokenInvalido(){
        Response response = delete(PRODUTOS_ENDPOINT+"/"+faker.internet().password(),"Bearer t0k3n f4ls0");
        assertThat(response.statusCode(),is(401));
        assertThat(response.body().path("message"),equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    public void deveFalharDeleteAdministradorFalse(){
        Usuario naoADM = UsuariosBuilder.normalUser();

        Response response = post(USUARIOS_ENDPOINT,naoADM);
        assertThat(response.statusCode(),is(201));
        naoADM.set_id(response.then().extract().path("_id"));
        Login login = new Login(naoADM.getEmail(),naoADM.getPassword());

        response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(200));
        String tempToken = response.then().extract().path("authorization");

        response = delete(PRODUTOS_ENDPOINT+"/dontcare",tempToken);
        assertThat(response.statusCode(),is(403));
        assertThat(response.body().path("message"),equalTo("Rota exclusiva para administradores"));
        response = delete(USUARIOS_ENDPOINT+"/"+naoADM.get_id());
        assertThat(response.statusCode(),is(200));
    }

    @Test
    public void deveFalharDeleteProdutoEmCarrinho(){
        prod = ProdutosBuilder.randomProduct();
        Response response = post(PRODUTOS_ENDPOINT,prod,token);
        assertThat(response.statusCode(),is(201));
        prod.set_id(response.then().extract().path("_id"));

        String payloadCarrinho = "{\n" +
                "  \"produtos\": [\n" +
                "    {\n" +
                "      \"idProduto\": \""+prod.get_id()+"\",\n" +
                "      \"quantidade\": 5\n" +
                "    }\n" +
                "  ]\n" +

                "}";

        given()
                .header("authorization",token)
                .and()
                .body(payloadCarrinho)
                .when()
                .post("/carrinhos")
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .and()
                .extract().path("_id");

        response = delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),equalTo("Não é permitido excluir produto que faz parte de carrinho"));

        given()
                .header("authorization",token)
                .when()
                .delete("/carrinhos/cancelar-compra")
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("message",containsString("Registro excluído com sucesso"));

        response = delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Registro excluído com sucesso"));
    }
}
