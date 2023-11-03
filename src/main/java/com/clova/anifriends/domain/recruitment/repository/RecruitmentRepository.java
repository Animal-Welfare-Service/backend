package com.clova.anifriends.domain.recruitment.repository;

import com.clova.anifriends.domain.recruitment.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query("select r from Recruitment r"
        + " where r.recruitmentId in ("
        + "select a.recruitment.recruitmentId from Applicant a"
        + " where a.volunteer.volunteerId = :volunteerId"
        + " and a.status = com.clova.anifriends.domain.applicant.wrapper.ApplicantStatus.ATTENDANCE)")
    Page<Recruitment> findCompletedRecruitments(
        @Param("volunteerId") Long volunteerId,
        Pageable pageable);
}
