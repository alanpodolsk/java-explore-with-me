package events.controller.pub;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.dto.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class EventsPubController {

    private final StatsClient statsClient;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/{id}")
    public void getEventById(@PathVariable Integer id, HttpServletRequest request) {
        HitDto hitDto = new HitDto(
                "ewm-main-service",
                "/events/" + id,
                request.getRemoteAddr(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
        statsClient.postHit(hitDto);
    }
}
