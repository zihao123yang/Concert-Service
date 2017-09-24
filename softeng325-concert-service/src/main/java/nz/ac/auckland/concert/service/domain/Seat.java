package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.service.domain.jpa.SeatNumberConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by zihaoyang on 22/09/17.
 */
@Entity
@Access(AccessType.FIELD)
public class Seat implements Serializable {

    @Id
    @Enumerated(EnumType.STRING)
    private SeatRow _seatRow;

    @Id
    @Convert(converter = SeatNumberConverter.class)
    private SeatNumber _seatNumber;

    @Id
    private LocalDateTime _date;

    public Seat() {

    }

    public Seat(SeatRow seatRow, SeatNumber seatNumber, LocalDateTime date) {
        _seatRow = seatRow;
        _seatNumber = seatNumber;
        _date = date;
    }

    public SeatRow getRow() {
        return _seatRow;
    }

    public SeatNumber getNumber() {
        return _seatNumber;
    }

    public LocalDateTime getDate() {
        return _date;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Seat))
            return false;
        if (obj == this)
            return true;

        Seat rhs = (Seat) obj;
        return new EqualsBuilder().
                append(_seatRow, rhs.getRow()).
                append(_seatNumber, rhs.getNumber()).
                append(_date, rhs.getDate()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_seatRow).
                append(_seatNumber).
                hashCode();
    }
}
