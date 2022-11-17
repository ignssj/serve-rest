package tests.Produtos.Post;

import builders.ProdutosBuilder;
import builders.UsuariosBuilder;
import io.restassured.response.Response;
import models.Login;
import models.Produto;
import template.TemplateProdutos;
import models.Usuario;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static constants.EndpointsPaths.LOGIN_ENDPOINT;
import static constants.EndpointsPaths.USUARIOS_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.github.javafaker.Faker;

public class ProdutoPostTest extends TemplateProdutos {

    private static String token;
    private static Usuario user;
    private static Produto prod;
    private static final String PRODUTOS_ENDPOINT = "/produtos";
    private static Faker faker = new Faker(Locale.ENGLISH);


    @BeforeAll
    public static void CadastrarELogarUsuario() {
        user = UsuariosBuilder.admUser();

        Response response = post(USUARIOS_ENDPOINT, user);
        assertThat(response.statusCode(), is(201));
        user.set_id(response.then().extract().path("_id"));

        Login login = new Login(user.getEmail(), user.getPassword());

        response = post(LOGIN_ENDPOINT, login);
        assertThat(response.statusCode(), is(200));
        token = response.then().extract().path("authorization");
    }

    @AfterAll
    public static void excluirUsuario(){
        Response response = delete(USUARIOS_ENDPOINT+"/"+user.get_id());
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Registro excluído com sucesso"));
    }

    @Test
    public void deveCadastrarProdutoSucesso(){
        prod = ProdutosBuilder.randomProduct();

        Response response = post(PRODUTOS_ENDPOINT,prod,token);
        prod.set_id(response.then().extract().path("_id"));
        assertThat(response.statusCode(),is(201));
        assertThat(response.body().path("message"),equalTo("Cadastro realizado com sucesso"));

        response = delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
        assertThat(response.body().path("message"),equalTo("Registro excluído com sucesso"));
        assertThat(response.statusCode(),is(200));
    }

    @Test
    public void deveFalharAoCadastrarProdutoIncompleto(){
        prod = ProdutosBuilder.emptyProduct();
        Response response = post(PRODUTOS_ENDPOINT,prod,token);
        assertThat(response.body().path("nome"),equalTo("nome não pode ficar em branco"));
        assertThat(response.body().path("descricao"),equalTo("descricao não pode ficar em branco"));
        assertThat(response.body().path("preco"),equalTo("preco deve ser um número positivo"));
        assertThat(response.body().path("quantidade"),equalTo("quantidade deve ser maior ou igual a 0"));
        assertThat(response.statusCode(),is(400));
    }

    @Test
    public void deveFalharCadastroTokenInvalido(){
        prod = ProdutosBuilder.randomProduct();
        Response response = post(PRODUTOS_ENDPOINT,prod,"Bearer t0k3n f4ls0");
        assertThat(response.body().path("message"),equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
        assertThat(response.statusCode(),is(401));
    }

    @Test
    public void deveFalharCadastroPorNomeEmUso(){
        prod = ProdutosBuilder.randomProduct();
        Response response = post(PRODUTOS_ENDPOINT,prod,token);
        assertThat(response.statusCode(),is(201));
        prod.set_id(response.then().extract().path("_id"));

        prod.setDescricao("Outra descricao");
        prod.setPreco(400);
        prod.setQuantidade(329);

        response = post(PRODUTOS_ENDPOINT,prod,token);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),equalTo("Já existe produto com esse nome"));

        response = delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Registro excluído com sucesso"));
    }

    @Test
    public void deveFalharPorAdmFalse(){

       Usuario falseAdm = UsuariosBuilder.normalUser();

       Response response = post(USUARIOS_ENDPOINT,falseAdm);
       assertThat(response.statusCode(),is(201));
       falseAdm.set_id(response.then().extract().path("_id"));

        Login login = new Login(falseAdm.getEmail(),falseAdm.getPassword());

        response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(200));

        String tempToken = response.then().extract().path("authorization");

        prod = ProdutosBuilder.randomProduct();

        response = post(PRODUTOS_ENDPOINT,prod,tempToken);
        assertThat(response.statusCode(),is(403));
        assertThat(response.body().path("message"),equalTo("Rota exclusiva para administradores"));

        response = super.delete(USUARIOS_ENDPOINT+"/"+falseAdm.get_id());
        assertThat(response.statusCode(),is(200));
    }


}
