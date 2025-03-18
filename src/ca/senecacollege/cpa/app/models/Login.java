/****************************************************************************************************
 * P1-Inventory-Manager - Login Class
 ****************************************************************************************************/
package ca.senecacollege.cpa.app.models;

/****************************************************************************************************
 * Model Class: Login - representing a set of login credentials
 * This class has two methods which match a username and password string passed to it against the
 * existing login credentials, if they match the methods return TRUE, FALSE otherwise.
 ****************************************************************************************************/
public class Login {
    private final String username = "name123";
    private final String password = "login2025";

    public boolean usernameMatcher(String srcUsername) {
        return srcUsername.trim().equals(username);
    }

    public boolean passwordMatcher(String srcPassword) {
        return srcPassword.trim().equals(password);
    }
}
