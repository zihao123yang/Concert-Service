package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by zihaoyang on 20/09/17.
 */
@Embeddable
@Access(AccessType.FIELD)
public class CreditCard {


    private CreditCardDTO.Type _type;
    private String _name;
    private String _number;
    private LocalDate _expiryDate;


    public CreditCard() {

    }

    public CreditCard(CreditCardDTO.Type type, String name, String number, LocalDate expiryDate) {

        _type = type;
        _name = name;
        _number = number;
        _expiryDate = expiryDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CreditCardDTO))
            return false;
        if (obj == this)
            return true;

        CreditCard rhs = (CreditCard) obj;
        return new EqualsBuilder().
                append(_type, rhs._type).
                append(_name, rhs._name).
                append(_number, rhs._number).
                append(_expiryDate, rhs._expiryDate).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_type).
                append(_name).
                append(_number).
                append(_expiryDate).
                hashCode();
    }



}
