package microservice.users.core.models;

import lombok.Getter;
import microservice.users.core.models.enums.UserRole;
import microservice.users.core.models.valueobjects.*;

import java.util.List;

@Getter
public class Customer extends User {
    private String firstName;
    private String LastName;
    private String cartId;
    private PhoneNumber phoneNumber;
    private List<Address> shippingAddresses;


    protected Customer(UserId id, Email email, Password password, UserRole role, PhoneNumber phoneNumber, String firstName, String lastName, String cartId, List<Address> shippingAddresses){
        super(id, email, password, role);
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.LastName = lastName;
        this.cartId = cartId;
        this.shippingAddresses = shippingAddresses;
    }
}
