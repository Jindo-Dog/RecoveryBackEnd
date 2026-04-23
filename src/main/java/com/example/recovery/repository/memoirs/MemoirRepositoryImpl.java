package com.example.recovery.repository.memoirs;

import com.example.recovery.domain.memoirs.Memoirs;
import com.example.recovery.domain.memoirs.QMemoirs;
import com.example.recovery.request.MemoirListRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemoirRepositoryImpl implements MemoirRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Memoirs> getMemoirsByRequest(MemoirListRequest request) {
        QMemoirs memoir = QMemoirs.memoirs;

        return queryFactory.selectFrom(memoir)
                .where(memoir.user.id.eq(request.getUserId()))
                .orderBy(memoir.date.desc())
                .fetch();
    }
}
