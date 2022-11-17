package tests.Login.Get;

import io.restassured.response.Response;
import template.TemplateGeral;
import org.junit.jupiter.api.Test;

import static constants.EndpointsPaths.LOGIN_ENDPOINT;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class LoginGetTest extends TemplateGeral {


    @Test
    public void deveFalharPorGet() // falta na documentacao
    {
        Response response = get(LOGIN_ENDPOINT);
        assertThat(response.body().path("message"),containsString("Não é possível realizar GET em /login."));
        assertThat(response.statusCode(),is(405));
    }
}
