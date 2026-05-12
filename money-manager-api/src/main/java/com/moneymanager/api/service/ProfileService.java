package com.moneymanager.api.service;

import com.moneymanager.api.dto.ProfileDTO;
import com.moneymanager.api.entity.ProfileEntity;
import com.moneymanager.api.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {

        // Check if email already exists
        if (profileRepository.findByEmail(profileDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Convert DTO to Entity
        ProfileEntity newProfile = toEntity(profileDTO);

        // Generate activation token
        newProfile.setActivationToken(UUID.randomUUID().toString());

        // Save to database
        newProfile = profileRepository.save(newProfile);

        // Send activation email
        String activationLink =
                "http://localhost:8080/api/v1.0/activate?token="
                        + newProfile.getActivationToken();

        String subject = "Activate your Money Manager account";

        String body =
                "Click on the following link to activate your account:\n\n"
                        + activationLink;

        // If email sending fails, registration should still succeed
        try {
            emailService.sendEmail(
                    newProfile.getEmail(),
                    subject,
                    body
            );
        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }

        // Return response DTO
        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
}