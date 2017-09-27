package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by zihaoyang on 23/09/17.
 */
@Entity
public class Booking {

    @Id
    @GeneratedValue
    private Long _id;

    @ManyToOne
    private Concert _concert;

    private LocalDateTime _dateTime;

    @OneToMany(cascade=CascadeType.PERSIST)
    private Set<Seat> _seats;

    @Enumerated(EnumType.STRING)
    private PriceBand _priceBand;

    public Booking () {

    }

    public Booking (Concert concert, LocalDateTime date, Set<Seat> seats, PriceBand priceBand) {
        _concert = concert;
        _dateTime = date;
        _seats = seats;
        _priceBand = priceBand;
    }

    public Concert getConcert() {
        return _concert;
    }

    public LocalDateTime getDate() {
        return _dateTime;
    }

    public Set<Seat> getSeats() {
        return _seats;
    }

    public PriceBand getPriceBand() {
        return _priceBand;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SeatDTO))
            return false;
        if (obj == this)
            return true;

        Booking rhs = (Booking) obj;
        return new EqualsBuilder().append(_id, rhs._id)
                .append(_concert, rhs._concert)
                .append(_dateTime, rhs._dateTime)
                .append(_seats, rhs._seats)
                .append(_priceBand, rhs._priceBand).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(_concert.getId())
                .append(_concert.getTitle()).append(_dateTime).append(_seats)
                .append(_priceBand).hashCode();
    }
}
