package nz.ac.auckland.concert.service.domain;

/**
 * Created by zihaoyang on 20/09/17.
 */

import nz.ac.auckland.concert.common.dto.UserDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import java.util.HashSet;
import java.util.Set;

@Entity (name = "USERS")
@Access(AccessType.FIELD)
public class User {

    @Id
    private String _userName;

    private String _password;

    private String _firstName;

    private String _lastName;

    private CreditCard _creditCard;

    public User() {

    }


    public User (String userName, String password, String firstName, String lastName) {
        _userName = userName;
        _password = password;
        _firstName = firstName;
        _lastName = lastName;
    }

    public String getUserName() {
        return _userName;
    }

    public String getPassword() {
        return _password;
    }

    public String getFirstName() {
        return _firstName;
    }

    public String getLastName() {
        return _lastName;
    }

    public void setCreditCard(CreditCard creditCard) {
        _creditCard = creditCard;
    }
    public void printAll() {
        System.out.println("username: " + _userName);
        System.out.println("password: " + _password);
        System.out.println("first name: " + _firstName);
        System.out.println("last name: " + _lastName);
    }

    public boolean hasCreditCard() {
        if (_creditCard != null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;
        if (obj == this)
            return true;

        User rhs = (User) obj;
        return new EqualsBuilder().
                append(_userName, rhs._userName).
                append(_password, rhs._password).
                append(_firstName, rhs._firstName).
                append(_lastName, rhs._lastName).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_userName).
                append(_password).
                append(_firstName).
                append(_password).
                hashCode();
    }

}
