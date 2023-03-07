package tests.Usuarios.Post;

import builders.UsuariosBuilder;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import template.TemplateGeral;
import models.Usuario;
import org.junit.jupiter.api.Test;
import template.TemplateUsuarios;

import static constants.EndpointsPaths.USUARIOS_ENDPOINT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UsuarioPostTest extends TemplateUsuarios {

    private static Usuario user;

    @Test
    public void deveCadastrarPostComSucesso() {
        user = UsuariosBuilder.admUser();
        Response response = post(USUARIOS_ENDPOINT,user);
        String idExtraido = response.then().extract().path("_id");
        assertThat(response.body().path("message"),equalTo("Cadastro realizado com sucesso"));
        assertThat(response.statusCode(),is(201));
        user.set_id(idExtraido);

        delete(USUARIOS_ENDPOINT+"/"+user.get_id()).then()
                .assertThat().statusCode(200);
    }

    @Test
    public void deveFalharPostEmailJaCadastrado() {
        user = UsuariosBuilder.admUser();
        Response response = post(USUARIOS_ENDPOINT,user);
        String idExtraido = response.then().extract().path("_id");
        user.set_id(idExtraido);

        assertThat(response.body().path("message"),equalTo("Cadastro realizado com sucesso"));
        assertThat(response.statusCode(),is(201));

        user.setNome(UsuariosBuilder.faker.name().fullName()); // altera nome e senha
        user.setPassword(UsuariosBuilder.faker.internet().password());

        response = post(USUARIOS_ENDPOINT,user);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),equalTo("Este email já está sendo usado"));

        delete(USUARIOS_ENDPOINT+"/"+user.get_id()).then()
                .assertThat().statusCode(200);
    }

    @Test
    public void deveFalharPostPorCamposVazios() {
        user = UsuariosBuilder.emptyUser();
        Response response = post(USUARIOS_ENDPOINT,user);
        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("email"),equalTo("email não pode ficar em branco"));
        assertThat(response.body().path("password"),equalTo("password não pode ficar em branco"));
        assertThat(response.body().path("nome"),equalTo("nome não pode ficar em branco"));
        assertThat(response.body().path("administrador"),equalTo("administrador deve ser 'true' ou 'false'"));
    }

    @Test
    public void deveFalharPostBodyVazio() {
        Response response = post(USUARIOS_ENDPOINT,"");
        assertThat(response.body().path("nome"),equalTo("nome é obrigatório"));
        assertThat(response.body().path("administrador"),equalTo("administrador é obrigatório"));
        assertThat(response.body().path("password"),equalTo("password é obrigatório"));
        assertThat(response.body().path("email"),equalTo("email é obrigatório"));
        assertThat(response.statusCode(),is(400));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    public void deveFalharPostPorGmail() {
        user = UsuariosBuilder.admUser();
        user.setEmail(UsuariosBuilder.faker.name().firstName()+"@gmail.com");
        Response response = post(USUARIOS_ENDPOINT,user);
        String idExtraido = response.then().extract().path("_id"); // extrai o id do usuário cadastrado
        user.set_id(idExtraido);
        delete(USUARIOS_ENDPOINT+"/"+user.get_id())
                .then().assertThat().statusCode(200);

        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),not(equalTo("Cadastro realizado com sucesso")));
   }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    public void deveFalharPostPorHotmail() {
        user = UsuariosBuilder.admUser();
        user.setEmail(UsuariosBuilder.faker.name().firstName()+"@hotmail.com");

        Response response = post(USUARIOS_ENDPOINT,user);
        String idExtraido = response.then().extract().path("_id"); // extrai o id do usuário cadastrado
        user.set_id(idExtraido);

        delete(USUARIOS_ENDPOINT+"/"+user.get_id())
                .then().assertThat().statusCode(200);

        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),not(equalTo("Cadastro realizado com sucesso")));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void deveFalharPostPorSenhaFraca() {
        user = UsuariosBuilder.admUser();
        user.setPassword(UsuariosBuilder.faker.internet().password(1,4));

        Response response = post(USUARIOS_ENDPOINT,user);
        String idExtraido = response.then().extract().path("_id"); // extrai o id do usuário cadastrado
        user.set_id(idExtraido);
        delete(USUARIOS_ENDPOINT+"/"+user.get_id())
                .then().assertThat().statusCode(200);

        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),not(equalTo("Cadastro realizado com sucesso")));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void deveFalharPostPorSenhaGrande() {
        user = UsuariosBuilder.admUser();
        user.setPassword(UsuariosBuilder.faker.internet().password(11,12));

        Response response = post(USUARIOS_ENDPOINT,user);
        String idExtraido = response.then().extract().path("_id"); // extrai o id do usuário cadastrado
        user.set_id(idExtraido);

        delete(USUARIOS_ENDPOINT+"/"+user.get_id())
                .then().assertThat().statusCode(200);

        assertThat(response.statusCode(),is(400));
        assertThat(response.body().path("message"),not(equalTo("Cadastro realizado com sucesso")));
    }
}
