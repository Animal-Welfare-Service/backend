package com.clova.anifriends.domain.recruitment.service;

import com.clova.anifriends.domain.common.dto.PageInfo;
import com.clova.anifriends.domain.recruitment.Recruitment;
import com.clova.anifriends.domain.recruitment.dto.response.FindCompletedRecruitmentsResponse;
import com.clova.anifriends.domain.recruitment.dto.response.FindRecruitmentByShelterResponse;
import com.clova.anifriends.domain.recruitment.dto.response.FindRecruitmentDetailByVolunteerResponse;
import com.clova.anifriends.domain.recruitment.dto.response.FindRecruitmentsByShelterIdResponse;
import com.clova.anifriends.domain.recruitment.dto.response.FindRecruitmentsByShelterResponse;
import com.clova.anifriends.domain.recruitment.dto.response.FindRecruitmentsByVolunteerResponse;
import com.clova.anifriends.domain.recruitment.dto.response.FindShelterSimpleResponse;
import com.clova.anifriends.domain.recruitment.dto.response.RegisterRecruitmentResponse;
import com.clova.anifriends.domain.recruitment.exception.RecruitmentNotFoundException;
import com.clova.anifriends.domain.recruitment.repository.RecruitmentRepository;
import com.clova.anifriends.domain.shelter.Shelter;
import com.clova.anifriends.domain.shelter.exception.ShelterNotFoundException;
import com.clova.anifriends.domain.shelter.repository.ShelterRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final ShelterRepository shelterRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Transactional
    public RegisterRecruitmentResponse registerRecruitment(
        Long shelterId,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime deadline,
        int capacity,
        String content,
        List<String> imageUrls) {
        Shelter shelter = getShelterById(shelterId);
        Recruitment recruitment = new Recruitment(
            shelter,
            title,
            capacity,
            content,
            startTime,
            endTime,
            deadline,
            imageUrls);
        recruitmentRepository.save(recruitment);
        return RegisterRecruitmentResponse.from(recruitment);
    }

    @Transactional(readOnly = true)
    public FindRecruitmentsByShelterResponse findRecruitmentsByShelter(
        Long shelterId,
        String keyword,
        LocalDate startDate,
        LocalDate endDate,
        Boolean content,
        Boolean title,
        Pageable pageable
    ) {
        Page<Recruitment> pagination = recruitmentRepository.findRecruitmentsByShelterOrderByCreatedAt(
            shelterId,
            keyword,
            startDate,
            endDate,
            content,
            title,
            pageable
        );

        return FindRecruitmentsByShelterResponse.of(pagination.getContent(),
            PageInfo.from(pagination));
    }

    @Transactional(readOnly = true)
    public FindRecruitmentsByShelterIdResponse findShelterRecruitmentsByShelter(
        long shelterId, Pageable pageable
    ) {
        Page<Recruitment> pagination = recruitmentRepository.findRecruitmentsByShelterId(
            shelterId, pageable
        );
        return FindRecruitmentsByShelterIdResponse.of(pagination.getContent(),
            PageInfo.from(pagination));
    }

    private Shelter getShelterById(Long shelterId) {
        return shelterRepository.findById(shelterId)
            .orElseThrow(() -> new ShelterNotFoundException("존재하지 않는 보호소입니다."));
    }

    public FindRecruitmentByShelterResponse findRecruitByShelter(
        long shelterId, long recruitmentId) {
        Recruitment recruitment = getRecruitmentByShelter(shelterId,
            recruitmentId);
        return FindRecruitmentByShelterResponse.from(recruitment);
    }

    public FindRecruitmentDetailByVolunteerResponse findRecruitmentByIdByVolunteer(long id) {
        Recruitment recruitment = getRecruitmentById(id);
        return FindRecruitmentDetailByVolunteerResponse.from(recruitment);
    }

    @Transactional(readOnly = true)
    public FindShelterSimpleResponse findShelterSimple(
        Long recruitmentId
    ) {
        Recruitment foundRecruitment = getRecruitmentById(recruitmentId);

        return FindShelterSimpleResponse.from(foundRecruitment);
    }

    private Recruitment getRecruitmentByShelter(long shelterId,
        long recruitmentId) {
        return recruitmentRepository.findByShelterIdAndRecruitmentId(shelterId, recruitmentId)
            .orElseThrow(() -> new RecruitmentNotFoundException("존재하지 않는 모집글입니다."));
    }

    private Recruitment getRecruitmentById(long id) {
        return recruitmentRepository.findById(id)
            .orElseThrow(() -> new RecruitmentNotFoundException("존재하지 않는 모집글입니다."));
    }

    @Transactional(readOnly = true)
    public FindCompletedRecruitmentsResponse findCompletedRecruitments(
        Long volunteerId,
        Pageable pageable) {
        Page<Recruitment> recruitmentPage
            = recruitmentRepository.findCompletedRecruitments(volunteerId, pageable);
        return FindCompletedRecruitmentsResponse.from(recruitmentPage);
    }

    @Transactional(readOnly = true)
    public FindRecruitmentsByVolunteerResponse findRecruitmentsByVolunteer(
        String keyword, LocalDate startDate, LocalDate endDate, Boolean isClosed,
        Boolean titleContains,
        Boolean contentContains, Boolean shelterNameContains, Pageable pageable) {
        Page<Recruitment> recruitments = recruitmentRepository.findRecruitments(
            keyword,
            startDate,
            endDate,
            isClosed,
            titleContains,
            contentContains,
            shelterNameContains,
            pageable);
        return FindRecruitmentsByVolunteerResponse.from(recruitments);
    }
}
