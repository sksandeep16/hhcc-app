package com.demo.service;

import com.demo.entity.FamilyMember;
import com.demo.repository.FamilyMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FamilyMemberService {

    private static final Logger log = LoggerFactory.getLogger(FamilyMemberService.class);

    private final FamilyMemberRepository repository;

    public FamilyMemberService(FamilyMemberRepository repository) {
        this.repository = repository;
    }

    public List<FamilyMember> getByUserId(Long userId) {
        log.debug("Fetching all family members for userId={}", userId);
        return repository.findByUserId(userId);
    }

    /** Search + filter family members for a user. Pass empty strings to skip a filter. */
    public List<FamilyMember> search(Long userId, String search, String relationship) {
        String like = (search == null || search.isBlank()) ? "" : "%" + search.trim() + "%";
        String rel  = (relationship == null) ? "" : relationship.trim();
        log.debug("Searching family members userId={} search='{}' relationship='{}'", userId, like, rel);
        return repository.search(userId, like, rel);
    }

    public FamilyMember add(Long userId, FamilyMember member) {
        log.info("Adding family member for userId={} name='{} {}'", userId, member.getFirstName(), member.getLastName());
        member.setUserId(userId);
        FamilyMember saved = repository.save(member);
        log.info("Family member saved with id={}", saved.getId());
        return saved;
    }

    public Optional<FamilyMember> update(Long userId, Long memberId, FamilyMember updated) {
        log.info("Updating family member id={} for userId={}", memberId, userId);
        return repository.findById(memberId).map(existing -> {
            if (!existing.getUserId().equals(userId)) {
                log.warn("User {} attempted to update member {} belonging to another user", userId, memberId);
                throw new IllegalArgumentException("Member does not belong to this user");
            }
            existing.setFirstName(updated.getFirstName());
            existing.setLastName(updated.getLastName());
            existing.setRelationship(updated.getRelationship());
            existing.setDateOfBirth(updated.getDateOfBirth());
            FamilyMember saved = repository.save(existing);
            log.info("Family member id={} updated successfully", memberId);
            return saved;
        });
    }

    public boolean delete(Long userId, Long memberId) {
        log.info("Deleting family member id={} for userId={}", memberId, userId);
        return repository.findById(memberId).map(existing -> {
            if (!existing.getUserId().equals(userId)) {
                log.warn("User {} attempted to delete member {} belonging to another user", userId, memberId);
                throw new IllegalArgumentException("Member does not belong to this user");
            }
            repository.deleteById(memberId);
            log.info("Family member id={} deleted", memberId);
            return true;
        }).orElseGet(() -> {
            log.warn("Family member id={} not found", memberId);
            return false;
        });
    }
}
