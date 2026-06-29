package com.disaster.safety.member.details;

import java.util.Map;

import com.disaster.safety.member.dto.OAuth2UserInfo;

public class GoogleUserDetails implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserDetails(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

}
