package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;

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

    public void showCardDetails() {
        System.out.println("name:" + _name);
        System.out.println("type:" + _type);
        System.out.println("number:" + _number);
    }


}
