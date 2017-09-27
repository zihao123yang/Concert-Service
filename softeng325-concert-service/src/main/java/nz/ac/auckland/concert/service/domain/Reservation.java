package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by zihaoyang on 21/09/17.
 */
@Entity
public class Reservation {

    @Id
    @GeneratedValue
    private Long _id;

    private int _numberOfSeats;

    @Enumerated(EnumType.STRING)
    private PriceBand _seatType;

    @ManyToOne
    @JoinColumn(name="CONCERT_ID")
    private Concert _concert;


    private LocalDateTime _date;

    @OneToMany(cascade=CascadeType.PERSIST)
    private Set<Seat> _seats;

    private Long _timestamp;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User _user;

    public Reservation() {

    }

    public Reservation(int numberOfSeats, PriceBand seatType, LocalDateTime date, Set<Seat> seats, Concert concert, User user, Long timestamp) {
        _numberOfSeats = numberOfSeats;
        _seatType = seatType;
        _date = date;
        _seats = seats;
        _concert = concert;
        _user = user;
        _timestamp = timestamp;
    }

    public Long getId() {
        return _id;
    }

    public LocalDateTime getDate() {
        return _date;
    }

    public Set<Seat> getSeats() {
        return _seats;
    }

    public PriceBand getPriceBand() {
        return _seatType;
    }

    public Long getTimestamp() {
        return _timestamp;
    }

    public User getUser() {
        return _user;
    }

    public Concert getConcert() {
        return _concert;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReservationDTO))
            return false;
        if (obj == this)
            return true;

        Reservation rhs = (Reservation) obj;
        return new EqualsBuilder().
                append(_id, rhs._id).
                append(_seats, rhs._seats).
                append(_seatType, rhs._seatType).
                append(_date, rhs._date).
                append(_concert, rhs._concert).
                append(_user, rhs._user).
                append(_timestamp, rhs._timestamp).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_id).
                append(_date).
                append(_seatType).
                append(_concert).
                append(_user).
                append(_seats).
                hashCode();
    }
}
