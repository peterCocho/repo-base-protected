package com.churninsight_dev.backend_api.dto;

import com.churninsight_dev.backend_api.model.Profile;
import java.util.Set;

public record UserInfoDTO(
    Long id,
    String userName,
    String email,
    String companyName,
    Set<Profile> profiles
) {}