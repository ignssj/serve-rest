package tests.Login.Post;

import com.github.javafaker.Faker;
import builders.UsuariosBuilder;
import io.restassured.response.Response;
import models.Login;
import template.TemplateGeral;
import models.Usuario;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static constants.EndpointsPaths.LOGIN_ENDPOINT;
import static constants.EndpointsPaths.USUARIOS_ENDPOINT;
import static helper.ServiceHelper.matcherJsonSchema;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LoginPostTest extends TemplateGeral {

    private static String _id;
    private static Usuario user;
    private static Faker faker = new Faker(Locale.ENGLISH);


    @BeforeAll
    public static void criarUsuario() {
        user = UsuariosBuilder.admUser();
        Response response = post(USUARIOS_ENDPOINT,user);
        String idExtraido = response.then().extract().path("_id");
        assertThat(response.statusCode(),is(201));
        user.set_id(idExtraido);
    }

    @AfterAll
    public static void deletarUsuario() {
        delete(USUARIOS_ENDPOINT+"/"+user.get_id()).then()
                .assertThat().statusCode(200);
    }


    @Test
    public void deveLogarComSucesso() // cadastra usuario, faz login e então exclui o usuário
    {
        Login login = new Login(user.getEmail(),user.getPassword());
        Response response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(200));
        assertThat(response.asString(),matcherJsonSchema("login","post",200));
        assertThat(response.body().path("message"),equalTo("Login realizado com sucesso"));
    }

    @Test
    public void deveFalharPorSenhaIncorreta() // garante que o email tem cadastro e falha por senha invalida
    {
        Login login = new Login(user.getEmail(),faker.internet().password(5,10));
        Response response = post(LOGIN_ENDPOINT,login);
        assertThat(response.asString(),matcherJsonSchema("login","post",400));
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),not(equalTo("Login realizado com sucesso")));
    }

    @Test
    public void deveFalharPorEmailNaoCadastrado() // tenta login com senha cadastrada porem email inexistente
    {
       Login login = new Login(faker.internet().emailAddress(),user.getPassword());
       Response response = post(LOGIN_ENDPOINT,login);
       assertThat(response.statusCode(),is(400));
       assertThat(response.body().path("message"),not(equalTo("Login realizado com sucesso")));
    }

    @Test
    public void deveFalharPorBodyVazio() // Json vazio
    {
        Response response = post(LOGIN_ENDPOINT,"");
        assertThat(response.body().path("password"),equalTo("password é obrigatório"));
        assertThat(response.body().path("email"),equalTo("email é obrigatório"));
        assertThat(response.statusCode(),is(400));
    }

    @Test
    public void deveFalharPorSenhaVazia() // falta na documentacao
    {
        Login login = new Login(user.getEmail(),"");
        Response response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("password"),equalTo("password não pode ficar em branco"));
    }

    @Test
    public void deveFalharPorEmailVazio() // falta na documentacao
    {
        Login login = new Login("",user.getPassword());
        Response response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("email"),equalTo("email não pode ficar em branco"));
    }

    @Test
    public void deveFalharPorCredenciaisVazias() // falta na documentacao
    {
        Login login = new Login("","");
        Response response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("email"),equalTo("email não pode ficar em branco"));
        assertThat(response.body().path("password"),equalTo("password não pode ficar em branco"));
    }

    @Test
    public void deveLogarDevolvendoBearerToken() // cadastra usuario, faz login e então exclui o usuário
    {
        Login login = new Login(user.getEmail(),user.getPassword());
        Response response = post(LOGIN_ENDPOINT,login);
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Login realizado com sucesso"));
        assertThat(response.body().path("authorization"),containsString("Bearer"));
    }

}
