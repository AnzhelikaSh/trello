package com.itpark.trello.repository;

import com.itpark.trello.model.Board;
import com.itpark.trello.model.User;  // ← Добавь импорт
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByOwnerId(Long ownerId);

    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.columns c LEFT JOIN FETCH c.tasks WHERE b.id = :id")
    Optional<Board> findByIdWithColumns(@Param("id") Long id);

    @Query("SELECT b FROM Board b JOIN b.members m WHERE m.id = :userId")
    List<Board> findBoardsByMemberId(@Param("userId") Long userId);

    List<Board> findByOwner(User owner);  // ← Убрал User.User
}

