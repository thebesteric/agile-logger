package agile.logger.example.web.quickstart;

import lombok.Data;

/**
 * Identity
 *
 * @author Eric Joe
 * @version 1.0
 */
@Data
public class Identity {
    private String username;
    private String password;
    private String identity;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
