package ru.practicum.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.users.model.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsersRepository extends JpaRepository<User, Integer> {
    List<User> getByIdIn(Integer[] ids);

    Page<User> getAll(Pageable pageable);
}
