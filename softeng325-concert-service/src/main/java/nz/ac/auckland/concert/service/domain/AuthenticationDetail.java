package nz.ac.auckland.concert.service.domain;

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
}
