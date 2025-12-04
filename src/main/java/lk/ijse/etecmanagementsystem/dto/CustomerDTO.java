package lk.ijse.etecmanagementsystem.dto;

public class CustomerDTO {

    private int Id;
    private String Name;
    private String Number;
    private String emailAddress;
    private String address;

    public CustomerDTO() {
    }

    public CustomerDTO(int id, String name, String number, String emailAddress, String address) {
        Id = id;
        Name = name;
        Number = number;
        this.emailAddress = emailAddress;
        this.address = address;
    }

    public CustomerDTO(String name, String number, String emailAddress, String address) {
        Name = name;
        Number = number;
        this.emailAddress = emailAddress;
        this.address = address;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Number='" + Number + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
