package com.clova.anifriends.domain.notification.service;

import com.clova.anifriends.domain.notification.ShelterNotification;
import com.clova.anifriends.domain.notification.dto.response.FindShelterNotificationsResponse;
import com.clova.anifriends.domain.notification.repository.ShelterNotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShelterNotificationService {

    private final ShelterNotificationRepository shelterNotificationRepository;

    @Transactional(readOnly = true)
    public FindShelterNotificationsResponse findShelterNotifications(Long shelterId) {
        List<ShelterNotification> shelterNotifications = shelterNotificationRepository.findByShelter_ShelterIdOrderByCreatedAtDesc(
            shelterId);
        return FindShelterNotificationsResponse.from(shelterNotifications);
    }
}
