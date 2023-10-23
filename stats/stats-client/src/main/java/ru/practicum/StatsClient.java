package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.dto.HitDto;

import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postHit(HitDto hitDto) {
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(HitDto hitDto, String start, String end, String[] uris, Boolean unique) {
        Map<String, Object> parameters = Map.of("start", start, "end", end);
        if (uris != null && uris.length > 0) {
            parameters.put("uris", uris);
        }
        if (unique != null) {
            parameters.put("unique", unique);
        }
        return get("/stats", parameters);
    }
}
