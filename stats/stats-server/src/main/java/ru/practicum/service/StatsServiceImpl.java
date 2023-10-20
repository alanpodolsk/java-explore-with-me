package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.exception.NoObjectException;
import ru.practicum.repository.HitsRepository;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Component
public class StatsServiceImpl implements StatsService {

    private final HitsRepository hitsRepository;
    private final StatsRepository statsRepository;

    @Override
    public HitDto saveHit(HitDto hitDto) {
        if (hitDto != null){
            return hitsRepository.save(hitDto);
        } else {
            throw new NoObjectException("Передан пустой Hit");
        }
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        if(start == null || end == null || uris.length == 0){
            throw new NoObjectException("Переданы не все параметры, необходимые для исполнения запроса");
        }
        if(unique){
            return statsRepository.getUniqueStats(start, end, uris);
        } else {
            return statsRepository.getNonUniqueStats(start, end, uris);
        }
    }
}
