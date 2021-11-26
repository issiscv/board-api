package com.example.boardapi.repository.board;

import com.example.boardapi.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository {
}
