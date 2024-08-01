package com.ca217;

import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UsersTable, String> {

    @Procedure(name = "UsersTable.signUpUser")
    String signUpUser(String p_Name, String p_Email, String p_Password, String p_User_Pic);
    @Procedure(name = "UsersTable.checkUser")
    String checkUser(String p_Email, String p_Password );
}
