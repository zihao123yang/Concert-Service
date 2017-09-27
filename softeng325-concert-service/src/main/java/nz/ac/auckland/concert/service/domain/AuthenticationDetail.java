package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.ws.rs.core.NewCookie;

/**
 * Created by zihaoyang on 22/09/17.
 */
@Entity
@Access(AccessType.FIELD)
public class AuthenticationDetail {

    @Id
    private String _token;

    @OneToOne
    private User _user;

    public AuthenticationDetail() {

    }

    public AuthenticationDetail(String token, User user) {
        _token = token;
        _user = user;
    }

    public String getToken() {
        return _token;
    }

    public User getUser() {
        return _user;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SeatDTO))
            return false;
        if (obj == this)
            return true;

        AuthenticationDetail rhs = (AuthenticationDetail) obj;
        return new EqualsBuilder().append(_token, rhs._token)
                .append(_user, rhs._user).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(_token).append(_user).hashCode();
    }

}
