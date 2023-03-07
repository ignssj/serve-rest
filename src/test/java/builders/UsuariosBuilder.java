package builders;

import com.github.javafaker.Faker;
import models.Produto;
import models.Usuario;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class UsuariosBuilder {
    public static Faker faker = new Faker(Locale.ENGLISH);

    public static Usuario admUser() {
        Usuario user = new Usuario();
        user.setNome(faker.leagueOfLegends().champion());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password(5, 10));
        user.setAdministrador("true");
        return user;
    }

    public static Usuario normalUser() {
        Usuario user = new Usuario();
        user.setNome(faker.leagueOfLegends().champion());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password(5, 10));
        user.setAdministrador("false");
        return user;
    }

    public static Usuario emptyUser() {
        Usuario user = new Usuario();
        user.setNome("");
        user.setEmail("");
        user.setPassword("");
        user.setAdministrador("");
        return user;
    }

}
