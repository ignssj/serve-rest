package helper;

import java.text.MessageFormat;
import java.util.Properties;

public class EnvironmentConfig {

    private static Properties environmentProperties;

    public static String getProperty(String key,String defaultValue){ // retorna propriedade de ambiente
    loadConfigurationProperties();
    return System.getProperty(key, environmentProperties.getProperty(key, defaultValue));
    }

    private static void loadConfigurationProperties(){ // carrega arquivo de configuração
        if(environmentProperties == null){
            String env = System.getProperty("env","local");
            String configFile = MessageFormat.format("/config/{0}.properties",env);
            environmentProperties = new Properties();

            try{
                environmentProperties.load(EnvironmentConfig.class.getResourceAsStream(configFile));
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("Erro na leitura do arquivo de configuração.");
            }
        }
    }
}
