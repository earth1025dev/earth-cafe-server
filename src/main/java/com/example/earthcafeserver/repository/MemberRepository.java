package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.member.Member;
import com.example.earthcafeserver.domain.member.MemberStatus;
import com.example.earthcafeserver.domain.member.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Member> findByPhone(String phone) {
        return em.createQuery("select m from Member m where m.phone = :phone", Member.class)
                .setParameter("phone", phone)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByPhone(String phone) {
        return em.createQuery("select m from Member m where m.phone = :phone", Member.class)
                .setParameter("phone", phone)
                .getResultStream()
                .findFirst()
                .isPresent();
    }

    public List<Member> findByStatus(MemberStatus status) {
        return em.createQuery("select m from Member m where m.status = :status", Member.class)
                .setParameter("status", status).getResultList();
    }

    public List<Member> findByRole(Role role) {
        return em.createQuery("select m from Member m where m.role = :role", Member.class)
                .setParameter("role", role).getResultList();
    }

    public List<Member> findByStatusAndWithdrawnAtAfter(MemberStatus status, LocalDateTime after) {
        LocalDateTime after30Days = after.plusDays(30);

        return em.createQuery("select m from Member m where m.status = :status and m.withdrawnAt > :after30Days", Member.class)
                .setParameter("status", status)
                .setParameter("after30Days", after30Days).getResultList();
    }

    public long countByStatus(MemberStatus status) {
        return em.createQuery("select count(m) from Member m where m.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    @Transactional
    public int updateStatusAndWithdrawnAt(Long memberId,
                                          MemberStatus status,
                                          LocalDateTime withdrawnAt) {
        return em.createQuery(
                        "update Member m set m.status = :status, m.withdrawnAt = :withdrawnAt where m.id = :id")
                .setParameter("status", status)
                .setParameter("withdrawnAt", withdrawnAt)
                .setParameter("id", memberId)
                .executeUpdate();
    }

    @Transactional
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Transactional
    public void delete(Member member) {
        em.remove(member);
    }
}
