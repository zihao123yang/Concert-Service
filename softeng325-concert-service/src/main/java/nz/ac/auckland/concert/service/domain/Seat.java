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

    public enum SeatStatus {
        Free, Reserved, Booked
    }

    @Version
    private long version;

    @Id
    @Enumerated(EnumType.STRING)
    private SeatRow _seatRow;

    @Id
    @Convert(converter = SeatNumberConverter.class)
    private SeatNumber _seatNumber;

    @Id
    private LocalDateTime _date;

    @Enumerated(EnumType.STRING)
    private SeatStatus _status;

    public Seat() {

    }

    public Seat(SeatRow seatRow, SeatNumber seatNumber, LocalDateTime date) {
        _seatRow = seatRow;
        _seatNumber = seatNumber;
        _date = date;
        _status = SeatStatus.Free;
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

    public void setReserved() {
        _status = SeatStatus.Reserved;
    }

    public void setBooked() {
        _status = SeatStatus.Booked;
    }

    public void setFree() {
        _status = SeatStatus.Free;
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
