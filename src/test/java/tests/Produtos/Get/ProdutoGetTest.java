package tests.Produtos.Get;

import builders.ProdutosBuilder;
import builders.UsuariosBuilder;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import models.Login;
import models.Produto;
import template.TemplateProdutos;
import models.Usuario;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static constants.EndpointsPaths.*;
import static helper.ServiceHelper.matcherJsonSchema;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.github.javafaker.Faker;

public class ProdutoGetTest extends TemplateProdutos {

    private static String token;
    private static Produto prod;
    private static Usuario user;
    private static Faker faker = new Faker(Locale.ENGLISH);

    @BeforeAll
    public static void LogarUsuarioECadastrarProduto(){
        user = UsuariosBuilder.admUser();
        Response response = post(USUARIOS_ENDPOINT,user);
        assertThat(response.statusCode(),is(201));
        user.set_id(response.then().extract().path("_id"));
        Login login = Login.of(user.getEmail(),user.getPassword());
        response = post(LOGIN_ENDPOINT,login);
        token = response.then().extract().path("authorization");
        assertThat(response.statusCode(),is(200));
        prod = ProdutosBuilder.randomProduct();
        response = post(PRODUTOS_ENDPOINT,prod,token);
        assertThat(response.statusCode(),is(201));
        assertThat(response.body().path("message"),equalTo("Cadastro realizado com sucesso"));
        prod.set_id(response.then().extract().path("_id"));
    }

    @AfterAll
    public static void deletarProdutoEUsuario(){
       Response response = delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
       assertThat(response.statusCode(),is(200));
       response = delete(USUARIOS_ENDPOINT+"/"+user.get_id());
       assertThat(response.statusCode(),is(200));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    public void deveMostrarTodosProdutos(){
        Response response = get(PRODUTOS_ENDPOINT);
        assertThat(response.asString(),matcherJsonSchema("produtos","get",200));
        assertThat(response.body().path("produtos.nome"),hasItem(prod.getNome()));
        assertThat(response.path("quantidade"),greaterThanOrEqualTo(1));
    }

    @Test
    public void deveMostrarProdutoCadastrado(){
        Produto produto = getProduct(PRODUTOS_ENDPOINT+"/"+prod.get_id());
        assertThat(produto.getNome(),is(prod.getNome()));
        assertThat(produto.getDescricao(),is(prod.getDescricao()));
        assertThat(produto.getPreco(),is(prod.getPreco()));
        assertThat(produto.getQuantidade(),is(prod.getQuantidade()));
        assertThat(produto,isA(Produto.class));
    }

    @Test
    public void deveFalharPorMostrarProdutoInexistente(){
        Response response = get(PRODUTOS_ENDPOINT+"/"+faker.internet().password());
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),is("Produto não encontrado"));
        assertThat(response.body().path("message"),isA(String.class));
    }
}
