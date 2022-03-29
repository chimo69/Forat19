package proyecto.golfus.forat19;

import java.io.Serializable;

public class Token implements Serializable {
    String user;
    String password;

    public Token(String user, String password) {
        this.user = user;
        this.password = password;
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
