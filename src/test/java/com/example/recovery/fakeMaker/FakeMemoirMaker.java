package com.example.recovery.fakeMaker;

import com.example.recovery.domain.memoirs.Memoirs;
import com.example.recovery.domain.user.Users;
import lombok.Builder;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Map;

public final class FakeMemoirMaker {

    private FakeMemoirMaker() {
    }

    public static Users user(String nickname, OffsetDateTime createdAt) {
        Users user = new Users();
        ReflectionTestUtils.setField(user, "nickname", nickname);
        ReflectionTestUtils.setField(user, "profileUrl", null);
        ReflectionTestUtils.setField(user, "createdAt", createdAt);
        ReflectionTestUtils.setField(user, "activation", true);
        return user;
    }

    public static Memoirs memoir(Users user, OffsetDateTime date) {
        return memoir(user, MemoirOptions.builder().date(date).build());
    }

    public static Memoirs memoir(Users defaultUser, MemoirOptions options) {
        MemoirOptions safeOptions = options == null ? MemoirOptions.builder().build() : options;

        Memoirs result = new Memoirs();
        result.setUsers(resolveUser(defaultUser, safeOptions.uid));
        result.setMemoir(safeOptions.memoir);
        result.setImprovement(safeOptions.improvement);
        result.setFeedback(safeOptions.feedback);
        result.setDate(safeOptions.date);
        return result;
    }

    private static Users resolveUser(Users defaultUser, Long uid) {
        if (uid == null) {
            return defaultUser;
        }
        Users user = new Users();
        ReflectionTestUtils.setField(user, "id", uid);
        return user;
    }

    @Builder
    public static final class MemoirOptions {
        private Long uid;
        private Map<String, Object> memoir;
        private Map<String, Object> improvement;
        private Map<String, Object> feedback;
        private OffsetDateTime date;
    }

    public static long setupDefaultTestData(TestEntityManager entityManager) {
        Users userA = user("user-a", OffsetDateTime.parse("2026-01-01T00:00:00+09:00"));
        Users userB = user("user-b", OffsetDateTime.parse("2026-01-01T00:00:00+09:00"));

        entityManager.persist(userA);
        entityManager.persist(userB);
        entityManager.flush();

        long userAId = (long) entityManager.getEntityManager()
                .getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .getIdentifier(userA);

        long userBId = (long) entityManager.getEntityManager()
                .getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .getIdentifier(userB);

        Users managedUserA = entityManager.find(Users.class, userAId);
        Users managedUserB = entityManager.find(Users.class, userBId);

        entityManager.persist(memoir(managedUserA, OffsetDateTime.parse("2026-01-03T12:00:00+09:00")));
        entityManager.persist(memoir(managedUserA, OffsetDateTime.parse("2026-01-01T12:00:00+09:00")));
        entityManager.persist(memoir(managedUserB, OffsetDateTime.parse("2026-01-02T12:00:00+09:00")));
        entityManager.flush();
        entityManager.clear();

        return userAId;
    }
}
