package nz.ac.auckland.concert.service.domain;

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
}
