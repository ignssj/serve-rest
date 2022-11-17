package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Usuario

{
    private String nome;
    private String email;
    private String password;
    private String administrador;
    @JsonIgnore
    private String _id;

}
