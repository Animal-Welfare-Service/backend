package com.clova.anifriends.domain.volunteer.support;

import com.clova.anifriends.domain.volunteer.Volunteer;
import com.clova.anifriends.domain.volunteer.wrapper.VolunteerGender;

public class VolunteerFixture {

    private static final Long VOLUNTEER_ID = 1L;
    private static final String EMAIL = "asdf@gmail.com";
    private static final String PASSWORD = "asdf1234";
    private static final String BIRTH_DATE = "1999-03-23";
    private static final String PHONE_NUMBER = "01012345678";
    private static final String GENDER = VolunteerGender.MALE.getName();
    private static final Integer TEMPERATURE = 36;
    private static final String NAME = "김봉사";

    public static Volunteer volunteer() {
        return new Volunteer(EMAIL, PASSWORD, BIRTH_DATE, PHONE_NUMBER, GENDER, NAME);
    }
}