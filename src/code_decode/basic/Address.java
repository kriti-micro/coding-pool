package code_decode.basic;

//Implementing Comparable overrides compareTo method -Natural Sorting (default)
public class Address implements Comparable<Address>{
    String street;
    int pincode;

    public Address(String street, int pincode) {
        this.street = street;
        this.pincode = pincode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", pincode=" + pincode +
                '}';
    }


    @Override
    public int compareTo(Address o) {
        return this.getPincode()-o.getPincode();
    }
}
