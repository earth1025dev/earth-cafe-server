package com.example.earthcafeserver.repository;

import com.example.earthcafeserver.domain.member.Member;
import com.example.earthcafeserver.domain.member.MemberStatus;
import com.example.earthcafeserver.domain.member.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Member> findById(Long memberId) {
        return em.createQuery("select m from Member m where m.id = :id", Member.class)
                .setParameter("id", memberId)
                .getResultStream()
                .findFirst();
    }

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

    public Optional<Member> findByStatusAndWithdrawnAtAfter(Long memberId, MemberStatus status, LocalDateTime after) {
        return em.createQuery("select m from Member m where m.id = :id and m.status = :status and m.withdrawnAt > :after", Member.class)
                .setParameter("id", memberId)
                .setParameter("status", status)
                .setParameter("after", after)
                .getResultStream().findFirst();
    }

    public long countByStatus(MemberStatus status) {
        return em.createQuery("select count(m) from Member m where m.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    @Transactional
    public int deleteMembersWithdrawnBefore(LocalDateTime threshold) {
        return em.createQuery(
                        "delete from Member m where m.status = :status and m.withdrawnAt < :threshold")
                .setParameter("status", MemberStatus.WITHDRAWN)
                .setParameter("threshold", threshold)
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
