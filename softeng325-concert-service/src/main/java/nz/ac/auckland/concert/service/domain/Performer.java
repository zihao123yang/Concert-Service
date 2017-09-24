package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.Genre;

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
}
