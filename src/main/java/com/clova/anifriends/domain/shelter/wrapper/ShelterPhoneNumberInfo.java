package com.clova.anifriends.domain.shelter.wrapper;

import com.clova.anifriends.domain.shelter.exception.ShelterBadRequestException;
import com.clova.anifriends.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShelterPhoneNumberInfo {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(
        "010-[0-9]{4}-[0-9]{4}|0[1-9]{1,2}-[0-9]{3,4}-[0-9]{4}");

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "spare_phone_number")
    private String sparePhoneNumber;

    public ShelterPhoneNumberInfo(String phoneNumber, String sparePhoneNumber) {
        validateNotNull(phoneNumber, sparePhoneNumber);
        validatePhoneNumberPattern(phoneNumber, sparePhoneNumber);
        this.phoneNumber = phoneNumber;
        this.sparePhoneNumber = sparePhoneNumber;
    }

    private void validateNotNull(String phoneNumber, String sparePhoneNumber) {
        if(Objects.isNull(phoneNumber)) {
            throw new ShelterBadRequestException("전화번호는 필수 항목입니다.");
        }
        if(Objects.isNull(sparePhoneNumber)) {
            throw new ShelterBadRequestException("임시 전화번호는 필수 항목입니다.");
        }
    }

    private void validatePhoneNumberPattern(String phoneNumber, String sparePhoneNumber) {
        if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new ShelterBadRequestException(ErrorCode.BAD_REQUEST, "잘못된 전화번호 입력값입니다.");
        }
        if (!PHONE_NUMBER_PATTERN.matcher(sparePhoneNumber).matches()) {
            throw new ShelterBadRequestException(ErrorCode.BAD_REQUEST, "잘못된 임시 전화번호 입력값입니다.");
        }
    }
}
