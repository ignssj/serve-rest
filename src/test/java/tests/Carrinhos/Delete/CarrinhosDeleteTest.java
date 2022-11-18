package tests.Carrinhos.Delete;

import builders.CarrinhosBuilder;
import builders.ProdutosBuilder;
import builders.UsuariosBuilder;
import com.github.javafaker.Faker;
import io.restassured.response.Response;
import models.Carrinho;
import models.Login;
import models.Produto;
import models.Usuario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import template.TemplateCarrinhos;
import template.TemplateProdutos;

import java.util.Locale;

import static constants.EndpointsPaths.*;
import static constants.EndpointsPaths.CARRINHOS_ENDPOINT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CarrinhosDeleteTest extends TemplateCarrinhos{
    private static String token;
    private static Produto prod;
    private static Usuario user;
    private static Carrinho carrinho;
    private static Faker faker = new Faker(Locale.ENGLISH);

    @BeforeAll
    public static void CadastrarUsuarioProdutoECarrinho(){
        user = UsuariosBuilder.admUser();
        Response response = post(USUARIOS_ENDPOINT,user);
        assertThat(response.statusCode(),is(201));
        user.set_id(response.then().extract().path("_id"));
        Login login = Login.of(user.getEmail(),user.getPassword());
        response = post(LOGIN_ENDPOINT,login);
        token = response.then().extract().path("authorization");
        assertThat(response.statusCode(),is(200));
        prod = ProdutosBuilder.randomProduct();
        response = TemplateProdutos.post(PRODUTOS_ENDPOINT,prod,token);
        assertThat(response.statusCode(),is(201));
        assertThat(response.body().path("message"),equalTo("Cadastro realizado com sucesso"));
        prod.set_id(response.then().extract().path("_id"));
    }

    @AfterAll
    public static void deletaProdutoEUsuario(){
        Response response = TemplateProdutos.delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
        assertThat(response.statusCode(),is(200));
        response = delete(USUARIOS_ENDPOINT+"/"+user.get_id());
        assertThat(response.statusCode(),is(200));
    }

    @Test
    public void deveRecusarDeleteEmRaiz(){
        Response response = delete(CARRINHOS_ENDPOINT);
        assertThat(response.statusCode(),is(405));
        assertThat(response.path("message"),containsString("Não é possível realizar DELETE em"));
    }

    @Test
    public void deveRecusarDeleteUrlEspecifico(){
        Response response = delete(CARRINHOS_ENDPOINT+"/"+faker.internet().password());
        assertThat(response.statusCode(),is(405));
        assertThat(response.path("message"),containsString("Não é possível realizar DELETE em"));
    }

    @Test
    public void deveCancelarCarrinho(){
        carrinho = CarrinhosBuilder.retornaUmCarrinho(prod);
        Response response = post(CARRINHOS_ENDPOINT,carrinho,token);
        assertThat(response.statusCode(),is(201));
        carrinho.set_id(response.then().extract().path("_id"));
        response = delete(CARRINHOS_ENDPOINT+"/"+"cancelar-compra",token);
        assertThat(response.statusCode(),is(200));
        assertThat(response.path("message"),equalTo("Registro excluído com sucesso. Estoque dos produtos reabastecido"));
    }

    @Test
    public void deveFalharCancelarCarrinhoInexistente(){
        Response response = delete(CARRINHOS_ENDPOINT+"/"+"cancelar-compra",token);
        assertThat(response.statusCode(),is(200));
        assertThat(response.path("message"),equalTo("Não foi encontrado carrinho para esse usuário"));
    }

    @Test
    public void deveFalharCancelarTokenInvalido(){
        Response response = delete(CARRINHOS_ENDPOINT+"/"+"cancelar-compra","Bearer t0k3nf4ls0");
        assertThat(response.statusCode(),is(401));
        assertThat(response.path("message"),equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    public void deveConcluirCarrinho(){
        carrinho = CarrinhosBuilder.retornaUmCarrinho(prod);
        Response response = post(CARRINHOS_ENDPOINT,carrinho,token);
        carrinho.set_id(response.then().extract().path("_id"));
        assertThat(response.statusCode(),is(201));

        response = delete(CARRINHOS_ENDPOINT+"/"+"concluir-compra",token);
        assertThat(response.statusCode(),is(200));
        assertThat(response.path("message"),equalTo("Registro excluído com sucesso"));
    }

    @Test
    public void deveFalharConcluirCarrinhoInexistente(){
        Response response = delete(CARRINHOS_ENDPOINT+"/"+"concluir-compra",token);
        assertThat(response.statusCode(),is(200));
        assertThat(response.path("message"),equalTo("Não foi encontrado carrinho para esse usuário"));
    }

    @Test
    public void deveFalharConcluirTokenInvalido(){
        Response response = delete(CARRINHOS_ENDPOINT+"/"+"concluir-compra","Bearer t0k3nf4ls0");
        assertThat(response.statusCode(),is(401));
        assertThat(response.path("message"),equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

}
