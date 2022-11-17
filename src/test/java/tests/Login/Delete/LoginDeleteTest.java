package tests.Login.Delete;

import io.restassured.response.Response;
import template.TemplateGeral;
import org.junit.jupiter.api.Test;

import static constants.EndpointsPaths.LOGIN_ENDPOINT;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LoginDeleteTest extends TemplateGeral {

    @Test
    public void deveFalharPorDelete()
    {
        Response response = delete(LOGIN_ENDPOINT);
        assertThat(response.body().path("message"),containsString("Não é possível realizar DELETE em /login."));
        assertThat(response.statusCode(),is(405));
    }
}
