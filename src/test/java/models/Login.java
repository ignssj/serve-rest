package models;


import lombok.*;

@Data
@AllArgsConstructor(staticName = "of")
@ToString(includeFieldNames = true)
public class Login {
    private String email;
    private String password;
}
