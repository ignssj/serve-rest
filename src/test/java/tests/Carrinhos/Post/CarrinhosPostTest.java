package tests.Carrinhos.Post;

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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CarrinhosPostTest extends TemplateCarrinhos {
    private static String token;
    private static Produto prod;
    private static Usuario user;
    private static Carrinho carrinho;

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
    public static void deletarProdutoEUsuario(){
        Response response = TemplateProdutos.delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
        assertThat(response.statusCode(),is(200));
        response = delete(USUARIOS_ENDPOINT+"/"+user.get_id());
        assertThat(response.statusCode(),is(200));
    }

    @Test
    public void deveCadastrarCarrinho(){
        carrinho = CarrinhosBuilder.retornaUmCarrinho(prod);
        Response response = post(CARRINHOS_ENDPOINT,carrinho,token);
        carrinho.set_id(response.then().extract().path("_id"));
        assertThat(response.statusCode(),is(201));
        assertThat(response.path("message"),equalTo("Cadastro realizado com sucesso"));
        response = delete(CARRINHOS_ENDPOINT+"/"+"cancelar-compra",token);
        assertThat(response.statusCode(),is(200));
    }

    @Test
    public void deveFalharCarrinhoComProdutoInvalido(){
        carrinho = CarrinhosBuilder.retornaCarrinhoProdutoInvalido("idInexistente");
        Response response = post(CARRINHOS_ENDPOINT,carrinho,token);
        carrinho.set_id(response.then().extract().path("_id"));
        assertThat(response.statusCode(),is(400));
        assertThat(response.path("message"),equalTo("Produto não encontrado"));
    }

    @Test
    public void deveFalharProdutosInsuficientes(){
        carrinho = CarrinhosBuilder.retornaUmCarrinhoInsuficiente(prod);
        Response response = post(CARRINHOS_ENDPOINT,carrinho,token);
        carrinho.set_id(response.then().extract().path("_id"));
        assertThat(response.statusCode(),is(400));
        assertThat(response.path("message"),equalTo("Produto não possui quantidade suficiente"));
    }

    @Test
    public void deveFalharProdutosDuplicados(){
        carrinho = CarrinhosBuilder.retornaUmCarrinhoDuplicado(prod);
        Response response = post(CARRINHOS_ENDPOINT,carrinho,token);
        carrinho.set_id(response.then().extract().path("_id"));
        assertThat(response.statusCode(),is(400));
        assertThat(response.path("message"),equalTo("Não é permitido possuir produto duplicado"));
    }

    @Test
    public void deveFalharMultiplosCarrinhos(){
        carrinho = CarrinhosBuilder.retornaUmCarrinho(prod);
        Response response = post(CARRINHOS_ENDPOINT,carrinho,token);
        carrinho.set_id(response.then().extract().path("_id"));
        assertThat(response.statusCode(),is(201));
        response = post(CARRINHOS_ENDPOINT,carrinho,token);
        assertThat(response.statusCode(),is(400));
        assertThat(response.path("message"),equalTo("Não é permitido ter mais de 1 carrinho"));
        response = delete(CARRINHOS_ENDPOINT+"/"+"cancelar-compra",token);
        assertThat(response.statusCode(),is(200));
    }

}
