package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hit hit = (Hit) o;
        return Objects.equals(app, hit.app) && Objects.equals(uri, hit.uri) && Objects.equals(ip, hit.ip) && Objects.equals(timestamp, hit.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(app, uri, ip, timestamp);
    }
}
