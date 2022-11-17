package tests.Usuarios.Put;

import com.github.javafaker.Faker;
import builders.UsuariosBuilder;
import io.restassured.response.Response;
import template.TemplateGeral;
import models.Usuario;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static constants.EndpointsPaths.USUARIOS_ENDPOINT;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UsuarioPutTest extends TemplateGeral {
    private static String _id;
    private static Usuario user;
    private static Faker faker = new Faker(Locale.ENGLISH);

    @BeforeAll
    public static void criarUsuario(){
        user = UsuariosBuilder.admUser();
        Response response = post(USUARIOS_ENDPOINT,user);
        assertThat(response.statusCode(),is(201));
        String idExtraido = response.then().extract().path("_id");
        user.set_id(idExtraido);
    }

    @AfterAll
    public static void deletarUsuario() {
        Response response = delete(USUARIOS_ENDPOINT + "/" + user.get_id());
        assertThat(response.statusCode(), is(200));
    }

    @Test
    public void deveAlterarCadastroComSucesso() {
        Usuario usuarioAlterado = UsuariosBuilder.admUser();
        Response response = put(USUARIOS_ENDPOINT+"/"+user.get_id(),usuarioAlterado);
        assertThat(response.statusCode(),is(200));
        assertThat(response.body().path("message"),equalTo("Registro alterado com sucesso"));
    }

    @Test
    public void deveCadastrarPutComSucesso() {
        Usuario usuarioTemporario = UsuariosBuilder.admUser();
        Response response = put(USUARIOS_ENDPOINT+"/"+usuarioTemporario.getPassword()+usuarioTemporario.getNome(),usuarioTemporario);
        assertThat(response.statusCode(),is(201));
        assertThat(response.body().path("message"),equalTo("Cadastro realizado com sucesso"));
        String id = response.then().extract().path("_id");
        usuarioTemporario.set_id(id);
        delete(USUARIOS_ENDPOINT+"/"+usuarioTemporario.get_id()).then()
                        .assertThat().statusCode(200);
    }

    @Test
    public void deveFalharPutEmailEmUso() {
        Usuario usuarioTemporario = UsuariosBuilder.admUser();
        usuarioTemporario.setEmail(user.getEmail());
        Response response = put(USUARIOS_ENDPOINT+"/"+user.getNome()+user.getPassword(),usuarioTemporario);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),equalTo("Este email já está sendo usado"));
    }

    @Test
    public void deveFalharPutCamposIncompletos() {
        Usuario usuarioTemporario = UsuariosBuilder.emptyUser();
        Response response = put(USUARIOS_ENDPOINT+"/"+user.get_id(),usuarioTemporario);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("nome"),equalTo("nome não pode ficar em branco"));
        assertThat(response.body().path("email"),equalTo("email não pode ficar em branco"));
        assertThat(response.body().path("password"),equalTo("password não pode ficar em branco"));
        assertThat(response.body().path("administrador"),equalTo("administrador deve ser 'true' ou 'false'"));
    }


    @Test
    public void deveFalharPutSenhaGrande() {
        Usuario usuarioTemporario = UsuariosBuilder.admUser();
        usuarioTemporario.setPassword(faker.internet().password(11,12));
        Response response = put(USUARIOS_ENDPOINT+"/"+user.get_id(),usuarioTemporario);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),not(equalTo("Registro alterado com sucesso")));
    }

    @Test
    public void deveFalharPutSenhaCurta() {
        Usuario usuarioTemporario = UsuariosBuilder.admUser();
        usuarioTemporario.setPassword(faker.internet().password(2,4));
        Response response = put(USUARIOS_ENDPOINT+"/"+user.get_id(),usuarioTemporario);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),not(equalTo("Registro alterado com sucesso")));
    }

}
