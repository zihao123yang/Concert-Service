package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.Genre;
import nz.ac.auckland.concert.common.types.PriceBand;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * Created by zihaoyang on 19/09/17.
 */
@Entity
@Table(name = "CONCERTS")
public class Concert {

    @Id
    @GeneratedValue
    private Long _id;

    private String _title;

    @ManyToMany
    @JoinTable(name="CONCERT_PERFORMER", joinColumns = @JoinColumn(name = "CONCERT_ID"), inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID"))
    private Set<Performer> _performers;

    @ElementCollection
    @CollectionTable(
            name = "CONCERT_DATES",
            joinColumns = @JoinColumn(name = "CONCERT_ID"))
    @Column(name = "DATES")
    private Set<LocalDateTime> _dates;

    @ElementCollection
    @CollectionTable(name="CONCERT_TARIFS", joinColumns = @JoinColumn(name = "CONCERT_ID"))
    @MapKeyEnumerated(EnumType.STRING)
    private Map<PriceBand, BigDecimal> _concertTarif;

    public Concert() {

    }

    public Concert(Long id, String title, Set<Performer> performers, Set<LocalDateTime> dates, Map<PriceBand,BigDecimal> concertTarifs) {

        _id = id;
        _title = title;
        _performers = performers;
        _dates = dates;
        _concertTarif = concertTarifs;
    }

    public Long getId() {
        return _id;
    }

    public String getTitle() {
        return _title;
    }

    public Set<Performer> getPerformers() {
        return _performers;
    }

    public Set<LocalDateTime> getLocalDateTimes() {
        return _dates;
    }

    public Map<PriceBand, BigDecimal> getConcertTarifs() {
        return _concertTarif;
    }

}
