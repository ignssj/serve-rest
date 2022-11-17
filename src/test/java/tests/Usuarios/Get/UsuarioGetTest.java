package tests.Usuarios.Get;

import com.github.javafaker.Faker;
import builders.UsuariosBuilder;
import io.restassured.response.Response;
import template.TemplateGeral;
import models.Usuario;
import org.junit.jupiter.api.*;


import static helper.ServiceHelper.matcherJsonSchema;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static constants.EndpointsPaths.*;

public class UsuarioGetTest extends TemplateGeral {
    private static Faker faker = new Faker(Locale.ENGLISH);
    private static Usuario user;

    @BeforeAll
    public static void criarUsuario(){
        user = UsuariosBuilder.admUser();
        Response response = post(USUARIOS_ENDPOINT,user);
        assertThat(response.statusCode(),is(201));
        assertThat(response.body().path("message"),equalTo("Cadastro realizado com sucesso"));
        String idExtraido = response.then().extract().path("_id");
        user.set_id(idExtraido);
    }

    @AfterAll
    public static void deletarUsuario(){
        delete(USUARIOS_ENDPOINT+"/"+user.get_id())
                .then()
                        .statusCode(200)
                                .assertThat()
                                        .body("message",equalTo("Registro excluído com sucesso"));
    }

    @Test
    public void deveListarUsuarios(){
        Response response = get(USUARIOS_ENDPOINT);
        assertThat(response.asString(),matcherJsonSchema("usuarios","get",200));
        assertThat(response.body().path("usuarios.nome"),hasItem(user.getNome()));
        assertThat(response.path("quantidade"),greaterThanOrEqualTo(1));
    }

    @Test
    public void deveMostrarUsuarioComSucesso(){
        Response response = get(USUARIOS_ENDPOINT+"/"+user.get_id());
        response.then().statusCode(200)
                                .body("password",equalTo(user.getPassword()))
                                                .body("nome",equalTo(user.getNome()))
                                                                .body("administrador",equalTo(user.getAdministrador()));
    }

    @Test
    public void deveFalharMostrarUsuarioInexistente(){
        Response response = get(USUARIOS_ENDPOINT+"/"+faker.internet().password()); // id inexistente
        response.then().statusCode(400)
                        .body("message",equalTo("Usuário não encontrado"));
    }
}
