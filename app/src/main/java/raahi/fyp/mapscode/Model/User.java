package raahi.fyp.mapscode.Model;

public class User {
    private String Name;
    private String Password;
    private String Email;
    private String NIC;
    private String Phone;


    public User() {
    }

    public User(String name, String password, String email, String NIC, String phone) {
        Name = name;
        Password = password;
        Email = email;
        this.NIC = NIC;
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getNIC() {
        return NIC;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
