package ru.practicum.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilations.model.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    Page<Compilation> getByPinned(Boolean pinned, Pageable pageable);

    Page<Compilation> findAll(Pageable pageable);
}
