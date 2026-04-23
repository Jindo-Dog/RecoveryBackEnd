package com.example.recovery.repository.memoirs;

import com.example.recovery.domain.memoirs.Memoirs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MemoirRepository extends JpaRepository<Memoirs, Long>, QuerydslPredicateExecutor<Memoirs>, MemoirRepositoryCustom {
}
