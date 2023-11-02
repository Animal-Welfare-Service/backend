package com.clova.anifriends.domain.recruitment.repository;

import static com.clova.anifriends.domain.recruitment.QRecruitment.recruitment;

import com.clova.anifriends.domain.recruitment.Recruitment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecruitmentRepositoryImpl implements
    RecruitmentRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Page<Recruitment> findRecruitments(String keyword, LocalDate startDate,
        LocalDate endDate, Boolean isClosed, boolean titleFilter, boolean contentFilter,
        boolean shelterNameFilter, Pageable pageable) {
        List<Recruitment> content = query.select(recruitment)
            .from(recruitment)
            .join(recruitment.shelter)
            .leftJoin(recruitment.applicants)
            .where(
                keywordSearch(keyword, titleFilter, contentFilter, shelterNameFilter),
                recruitmentIsClosed(isClosed),
                recruitmentStartTimeGoe(startDate),
                recruitmentStartTimeLoe(endDate)
            )
            .orderBy(recruitment.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        Long count = query.select(recruitment.count())
            .from(recruitment)
            .join(recruitment.shelter)
            .where(
                keywordSearch(keyword, titleFilter, contentFilter, shelterNameFilter),
                recruitmentIsClosed(isClosed),
                recruitmentStartTimeGoe(startDate),
                recruitmentStartTimeLoe(endDate)
            ).fetchOne();
        return new PageImpl<>(content, pageable, count);
    }

    private BooleanBuilder keywordSearch(String keyword, boolean titleFilter,
        boolean contentFilter, boolean shelterNameFilter) {
        return nullSafeBuilder(() -> recruitmentTitleContains(keyword, titleFilter))
            .or(nullSafeBuilder(() -> recruitmentContentContains(keyword, contentFilter)))
            .or(nullSafeBuilder(() -> recruitmentShelterNameContains(keyword, shelterNameFilter)));
    }

    private BooleanExpression recruitmentTitleContains(String keyword, boolean titleFilter) {
        if (!titleFilter) {
            return null;
        }
        return keyword != null ? recruitment.title.title.contains(keyword) : null;
    }

    private BooleanExpression recruitmentContentContains(String keyword, boolean contentFilter) {
        if (!contentFilter) {
            return null;
        }
        return keyword != null ? recruitment.content.content.contains(keyword) : null;
    }

    private BooleanExpression recruitmentShelterNameContains(String keyword, boolean shelterNameFilter) {
        if (!shelterNameFilter) {
            return null;
        }
        return keyword != null ? recruitment.shelter.name.name.contains(keyword) : null;
    }

    private BooleanExpression recruitmentIsClosed(Boolean isClosed) {
        if (Objects.isNull(isClosed)) {
            return null;
        }
        return recruitment.info.isClosed.eq(isClosed);
    }

    private BooleanExpression recruitmentStartTimeGoe(LocalDate startDate) {
        return startDate != null ? recruitment.info.startTime.goe(startDate.atStartOfDay()) : null;
    }

    private BooleanExpression recruitmentStartTimeLoe(LocalDate endDate) {
        return endDate != null ? recruitment.info.startTime.loe(endDate.plusDays(1).atStartOfDay()) : null;
    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> supplier) {
        try {
            return new BooleanBuilder(supplier.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }
}