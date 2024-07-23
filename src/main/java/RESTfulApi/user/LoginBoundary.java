package RESTfulApi.user;

public class LoginBoundary {
    private String superapp;
    private String email;
    private String password;

    public LoginBoundary() {
    }

    public LoginBoundary(String superapp, String email, String password) {
        this.superapp = superapp;
        this.email = email;
        this.password = password;
    }

    public String getSuperapp() {
        return superapp;
    }

    public void setSuperapp(String superapp) {
        this.superapp = superapp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
