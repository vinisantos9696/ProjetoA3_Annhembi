package br.com.projeto.model;

/**
 * Classe modelo para representar um Usuário.
 */
public class User {

    private int id;
    private String fullName;
    private String email;
    private String login;
    private String password;
    private String profile; // (administrador, gerente, colaborador)

    // Construtor
    public User(int id, String fullName, String email, String login, String password, String profile) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.login = login;
        this.password = password;
        this.profile = profile;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    // Sobrescrevendo o método toString para facilitar a exibição em componentes Swing (como JComboBox)
    @Override
    public String toString() {
        return getFullName(); // Exibe o nome completo do usuário
    }
}