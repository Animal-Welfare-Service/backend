package com.clova.anifriends.domain.recruitment.controller;

import com.clova.anifriends.domain.recruitment.dto.response.FindRecruitmentResponse;
import com.clova.anifriends.domain.recruitment.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitments")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @GetMapping("/{recruitmentId}")
    public ResponseEntity<FindRecruitmentResponse> findRecruitmentById(@PathVariable Long recruitmentId) {
        return ResponseEntity.ok(recruitmentService.findRecruitmentById(recruitmentId));
    }
}
