package com.ca217;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
@NamedStoredProcedureQuery(
        name = "signUpUser",
        procedureName = "signUpUser",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_Name", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_Email", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_Password", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_User_Pic", type = String.class)
        }
)
@NamedStoredProcedureQuery(
        name = "checkUser",
        procedureName = "checkUser",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_Email", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_Password", type = String.class),
        }
)
public class UsersTable {

    @Id
    @Column(name = "Email")
    private String email;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "User_Pic", columnDefinition = "TEXT")
    private String userPic;

}
