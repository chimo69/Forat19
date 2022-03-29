package proyecto.golfus.forat19;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {
    String name = null;
    String id = null;
    String password = null;
    String mail = null;
    String phone=null;
    String address=null;
    Boolean info=false;

    public User(String name, String id, String password, String mail, String phone, String address, Boolean info) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.mail = mail;
        this.phone = phone;
        this.address = address;
        this.info = info;
    }
    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getInfo() {
        return info;
    }

    public void setInfo(Boolean info) {
        this.info = info;
    }


}
