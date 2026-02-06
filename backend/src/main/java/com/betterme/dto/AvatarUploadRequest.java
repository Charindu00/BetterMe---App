package com.betterme.dto;

import lombok.Data;

@Data
public class AvatarUploadRequest {
    private String avatar; // Base64 encoded image
}
