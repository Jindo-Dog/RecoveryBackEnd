package com.example.recovery.repository.memoirs;

import com.example.recovery.config.QuerydslConfig;
import com.example.recovery.domain.memoirs.Memoirs;
import com.example.recovery.domain.user.Users;
import com.example.recovery.fakeMaker.FakeMemoirMaker;
import com.example.recovery.request.MemoirListRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(QuerydslConfig.class)
class MemoirRepositoryImplTest {

    @Autowired
    private MemoirRepository memoirRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("회고 목록 조회 - userId 조건에 맞는 memoir를 date desc로 조회한다")
    void getMemoirsByRequest_filtersByUserAndSortsByDateDesc() {
        // given
        Users userA = persistUser("user-a", OffsetDateTime.parse("2026-01-01T00:00:00+09:00"));
        Users userB = persistUser("user-b", OffsetDateTime.parse("2026-01-01T00:00:00+09:00"));
        long userAId = extractId(userA);
        long userBId = extractId(userB);

        entityManager.persist(FakeMemoirMaker.memoir(null, FakeMemoirMaker.MemoirOptions.builder()
                .uid(userAId)
                .date(OffsetDateTime.parse("2026-01-03T12:00:00+09:00"))
                .build()
        ));
        entityManager.persist(FakeMemoirMaker.memoir(null, FakeMemoirMaker.MemoirOptions.builder()
                .uid(userAId)
                .date(OffsetDateTime.parse("2026-01-01T12:00:00+09:00"))
                .build()
        ));
        entityManager.persist(FakeMemoirMaker.memoir(null, FakeMemoirMaker.MemoirOptions.builder()
                .uid(userBId)
                .date(OffsetDateTime.parse("2026-01-02T12:00:00+09:00"))
                .build()
        ));

        entityManager.flush();
        entityManager.clear();

        MemoirListRequest request = new MemoirListRequest();
        request.setUserId(userAId);

        // when
        List<Memoirs> result = memoirRepository.getMemoirsByRequest(request);

        // then
        assertEquals(2, result.size());
        assertEquals(OffsetDateTime.parse("2026-01-03T12:00:00+09:00"), result.get(0).getDate());
        assertEquals(OffsetDateTime.parse("2026-01-01T12:00:00+09:00"), result.get(1).getDate());
    }

    @Test
    @DisplayName("조회 결과가 없으면 빈 리스트를 반환한다")
    void getMemoirsByRequest_returnsEmptyListWhenNoRows() {
        // given
        Users userA = persistUser("user-a", OffsetDateTime.parse("2026-01-01T00:00:00+09:00"));
        entityManager.persist(FakeMemoirMaker.memoir(null, FakeMemoirMaker.MemoirOptions.builder()
                .uid(extractId(userA))
                .date(OffsetDateTime.parse("2026-01-05T12:00:00+09:00"))
                .build()
        ));
        entityManager.flush();
        entityManager.clear();

        MemoirListRequest request = new MemoirListRequest();
        request.setUserId(999L);

        // when
        List<Memoirs> result = memoirRepository.getMemoirsByRequest(request);

        // then
        assertTrue(result.isEmpty());
    }

    private Users persistUser(String nickname, OffsetDateTime createdAt) {
        Users user = FakeMemoirMaker.user(nickname, createdAt);
        entityManager.persist(user);
        entityManager.flush();
        return entityManager.find(Users.class, extractId(user));
    }

    private long extractId(Users user) {
        return (long) entityManager.getEntityManager()
                .getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .getIdentifier(user);
    }
}