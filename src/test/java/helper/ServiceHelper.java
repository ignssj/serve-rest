package helper;

import io.restassured.module.jsv.JsonSchemaValidator;

import java.io.InputStream;
import java.security.Provider;
import java.text.MessageFormat;

public class ServiceHelper
{
    public static InputStream jsonSchemaStream(String endpoint, String schema, int status){
        // Metodo utilizado para criar o caminho para um arquivo de SCHEMA JSON
    String path = "/schemas/{0}/{1}/{2}.json";
    path = MessageFormat.format(path,endpoint,schema,status);
    return ServiceHelper.class.getResourceAsStream(path);
    }

    public static JsonSchemaValidator matcherJsonSchema(String endpoint, String schema, int status){
        // Matcher para validar um schema JSOn de acordo com path recebido
        InputStream schemaToMatch = jsonSchemaStream(endpoint,schema,status);
        return JsonSchemaValidator.matchesJsonSchema(schemaToMatch);
    }
}
