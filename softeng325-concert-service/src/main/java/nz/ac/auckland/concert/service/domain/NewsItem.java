package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Created by zihaoyang on 25/09/17.
 */
@Entity
public class NewsItem {

    @Id
    private Long _id;

    private LocalDateTime _timestamp;

    private String _content;

    public NewsItem() {}

    public NewsItem(Long id, LocalDateTime timestamp, String content) {
        _id = id;
        _timestamp = timestamp;
        _content = content;
    }

    public Long getId() {
        return _id;
    }

    public LocalDateTime getTimeStamp() {
        return _timestamp;
    }

    public String getContent() {
        return _content;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NewsItemDTO))
            return false;
        if (obj == this)
            return true;

        NewsItem rhs = (NewsItem) obj;
        return new EqualsBuilder().
                append(_timestamp, rhs._timestamp).
                append(_content, rhs._content).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_timestamp).
                append(_content).
                hashCode();
    }
}
