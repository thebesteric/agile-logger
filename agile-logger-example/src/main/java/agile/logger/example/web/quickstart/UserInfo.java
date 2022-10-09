package agile.logger.example.web.quickstart;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * UserInfo
 *
 * @author Eric Joe
 * @version 1.0
 */
public class UserInfo {
    private String username;
    private String password;
    private String greeting;

    public UserInfo() {
        super();
    }

    public UserInfo(String username, String password, String identity) {
        this.username = username;
        this.password = password;
        this.greeting = identity + ": hello " + username;
    }

    @JsonIgnore
    private String version = "0";

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

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
