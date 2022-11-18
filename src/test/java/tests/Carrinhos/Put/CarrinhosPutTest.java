package tests.Carrinhos.Put;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import models.Carrinho;
import models.Produto;
import models.Usuario;
import org.junit.jupiter.api.Test;
import template.TemplateCarrinhos;

import java.util.Locale;

import static constants.EndpointsPaths.CARRINHOS_ENDPOINT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CarrinhosPutTest extends TemplateCarrinhos {

    @Test
    public void deveRecusarMetodoPut(){
        Response response = put(CARRINHOS_ENDPOINT);
        assertThat(response.statusCode(),is(405));
        assertThat(response.path("message"),containsString("Não é possível realizar PUT em"));
    }

}
