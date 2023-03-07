package tests.Carrinhos.Get;

import builders.CarrinhosBuilder;
import builders.ProdutosBuilder;
import builders.UsuariosBuilder;
import com.github.javafaker.Faker;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
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
import static helper.ServiceHelper.matcherJsonSchema;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CarrinhosGetTest extends TemplateCarrinhos {
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
        carrinho = CarrinhosBuilder.retornaUmCarrinho(prod);
        response = post(CARRINHOS_ENDPOINT,carrinho,token);
        assertThat(response.statusCode(),is(201));
        carrinho.set_id(response.then().extract().path("_id"));
    }

    @AfterAll
    public static void deletarProdutoEUsuario(){
        Response response = delete(CARRINHOS_ENDPOINT+"/"+"cancelar-compra",token);
        assertThat(response.statusCode(),is(200));
        response = TemplateProdutos.delete(PRODUTOS_ENDPOINT+"/"+prod.get_id(),token);
        assertThat(response.statusCode(),is(200));
        response = delete(USUARIOS_ENDPOINT+"/"+user.get_id());
        assertThat(response.statusCode(),is(200));
    }

    @Test
    public void deveListarCarrinhos(){
        Response response = get(CARRINHOS_ENDPOINT);
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("carrinhos._id"),hasItem(carrinho.get_id()));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    public void devoValidarSchemaCarrinhosGet(){
        Response responseSchema = get(CARRINHOS_ENDPOINT);
        assertThat(responseSchema.asString(),matcherJsonSchema("carrinhos","get",200));
    }
    @Test
    public void devoMostrarCarrinhoExistente(){
        Response response = get(CARRINHOS_ENDPOINT+"/"+carrinho.get_id());
        Carrinho carrinhoDesserializado = response.as(Carrinho.class);
        assertThat(response.statusCode(),is(200));
        assertThat(response.path("_id"),equalTo(carrinho.get_id()));
        assertThat(response.path("idUsuario"),equalTo(user.get_id()));
    }

    @Test
    public void devoFalharCarrinhoInexistente(){
        Response response = get(CARRINHOS_ENDPOINT+"/"+faker.internet().password());
        assertThat(response.statusCode(),is(400));
        assertThat(response.path("message"),equalTo("Carrinho não encontrado"));
    }




}
