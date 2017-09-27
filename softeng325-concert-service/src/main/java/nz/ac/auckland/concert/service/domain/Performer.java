package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.types.Genre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by zihaoyang on 19/09/17.
 */
@Entity
@Table(name = "PERFORMERS")
public class Performer {

    @Id
    @GeneratedValue
    private Long _id;

    @Enumerated(EnumType.STRING)
    private Genre _genre;

    private String _image;

    private String _name;

    @ManyToMany(mappedBy = "_performers")
    private Set<Concert> _concerts;

    public Performer() {

    }

    public Long getId() {
        return _id;
    }

    public Genre getGenre() {
        return _genre;
    }

    public String getImage() {
        return _image;
    }

    public String getName() {
        return _name;
    }

    public Set<Concert> getConcerts() {
        return _concerts;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PerformerDTO))
            return false;
        if (obj == this)
            return true;

        Performer rhs = (Performer) obj;
        return new EqualsBuilder().
                append(_name, rhs._name).
                append(_id, rhs._id).
                append(_genre, rhs._genre).
                append(_image, rhs._image).
                append(_concerts, rhs._concerts).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_id).
                append(_name).
                append(_image).
                append(_genre).
                hashCode();
    }
}
