package tests.Produtos.Put;
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
import template.TemplateGeral;
import template.TemplateProdutos;

public class ProdutoPutTest extends TemplateProdutos {

    private static String token;
    private static Usuario user;
    private static Produto prod;

    private static Faker faker = new Faker(Locale.ENGLISH);


    @BeforeAll
    public static void LogarUsuarioECadastrarProduto(){
        user = UsuariosBuilder.admUser();

        Response response = post(USUARIOS_ENDPOINT,user);
        assertThat(response.statusCode(),is(201));
        user.set_id(response.then().extract().path("_id"));

        Login login = new Login(user.getEmail(),user.getPassword());
        response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(200));
        token = response.then().extract().path("authorization");

        prod = ProdutosBuilder.randomProduct();
        response = post(PRODUTOS_ENDPOINT,prod,token);
        assertThat(response.statusCode(),is(201));
        prod.set_id(response.then().extract().path("_id"));
        }

    @AfterAll
    public static void excluirUsuarioEProduto(){
        Response response = delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
        assertThat(response.statusCode(),is(200));
        response = TemplateGeral.delete(USUARIOS_ENDPOINT+"/"+user.get_id());
        assertThat(response.statusCode(),is(200));
    }

    @Test
    public void deveEditarProduto(){
        Produto produtoEditado = ProdutosBuilder.randomProduct();
        Response response = put(PRODUTOS_ENDPOINT+"/"+prod.get_id(),prod,token);
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Registro alterado com sucesso"));
    }

    @Test
    public void deveFalharEditProdutoIncompleto(){
        Produto produtoEditado = ProdutosBuilder.emptyProduct();
        Response response = put(PRODUTOS_ENDPOINT+"/"+prod.get_id(),produtoEditado,token);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("descricao"),equalTo("descricao não pode ficar em branco"));
        assertThat(response.body().path("nome"),equalTo("nome não pode ficar em branco"));
    }

    @Test
    public void deveFalharEditNomeEmUso(){
        Produto produtoEditado = ProdutosBuilder.randomProduct();
        produtoEditado.setNome(prod.getNome());

        Response response = put(PRODUTOS_ENDPOINT+"/"+faker.internet().password(),produtoEditado,token);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),equalTo("Já existe produto com esse nome"));
    }

    @Test
    public void deveCadastrarComPut(){
        Produto produtoNovo = ProdutosBuilder.randomProduct();
        Response response = put(PRODUTOS_ENDPOINT+"/"+faker.internet().password(),produtoNovo,token);
        assertThat(response.statusCode(),is(201));
        assertThat(response.body().path("message"),equalTo("Cadastro realizado com sucesso"));
        produtoNovo.set_id(response.body().path("_id"));

        response = delete(PRODUTOS_ENDPOINT+"/"+produtoNovo.get_id(),token);
        assertThat(response.statusCode(),is(200));
    }

    @Test
    public void deveFalharEditTokenInvalido(){
        Produto produtoEditado = ProdutosBuilder.randomProduct();
        Response response = put(PRODUTOS_ENDPOINT+"/"+prod.get_id(),produtoEditado,"Bearer 1nv4l1d0");
        assertThat(response.statusCode(),is(401));
        assertThat(response.body().path("message"),equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }
}
