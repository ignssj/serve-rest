package tests.Login.Put;

import io.restassured.response.Response;
import template.TemplateGeral;
import org.junit.jupiter.api.Test;
import template.TemplateLogin;

import static constants.EndpointsPaths.LOGIN_ENDPOINT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class LoginPutTest extends TemplateLogin {

    @Test
    public void deveFalharPorPut() // falta na documentacao
    {
        Response response = put(LOGIN_ENDPOINT,"");
        assertThat(response.body().path("message"),containsString("Não é possível realizar PUT em /login."));
        assertThat(response.statusCode(),is(405));
    }
}
