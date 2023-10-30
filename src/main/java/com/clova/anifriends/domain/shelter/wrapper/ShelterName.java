package com.clova.anifriends.domain.shelter.wrapper;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class ShelterName {

    @Column(name = "name")
    private String name;

    protected ShelterName() {
    }

    public ShelterName(String value) {
        this.name = value;
    }

}
