package com.clova.anifriends.domain.animal.repository;

import com.clova.anifriends.domain.animal.Animal;
import com.clova.anifriends.domain.animal.AnimalAge;
import com.clova.anifriends.domain.animal.AnimalSize;
import com.clova.anifriends.domain.animal.vo.AnimalActive;
import com.clova.anifriends.domain.animal.vo.AnimalGender;
import com.clova.anifriends.domain.animal.vo.AnimalNeuteredFilter;
import com.clova.anifriends.domain.animal.vo.AnimalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnimalRepositoryCustom {

    Page<Animal> findAnimalsByShelter(
        Long shelterId,
        String keyword,
        AnimalType type,
        AnimalGender gender,
        AnimalNeuteredFilter neuteredFilter,
        AnimalActive active,
        AnimalSize size,
        AnimalAge age,
        Pageable pageable
    );

    Page<Animal> findAnimalsByVolunteer(
        AnimalType type,
        AnimalActive active,
        AnimalNeuteredFilter neuteredFilter,
        AnimalAge age,
        AnimalGender gender,
        AnimalSize size,
        Pageable pageable
    );
}
