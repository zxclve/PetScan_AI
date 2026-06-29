package com.disaster.safety.member.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.disaster.safety.member.details.CustomOauth2UserDetails;
import com.disaster.safety.member.details.GoogleUserDetails;
import com.disaster.safety.member.dto.OAuth2UserInfo;
import com.disaster.safety.member.entity.Member;
import com.disaster.safety.member.entity.RoleType;
import com.disaster.safety.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(provider, oAuth2User);

        String providerId = oAuth2UserInfo.getProviderId();
        String userId = provider + "_" + providerId;
        String userName = oAuth2UserInfo.getName();

        Member member = memberRepository.findByUserId(userId)
                .orElseGet(() -> createOAuthMember(userId, userName, provider, providerId));

        return new CustomOauth2UserDetails(member, oAuth2User.getAttributes());
    }

    private OAuth2UserInfo getOAuth2UserInfo(String provider, OAuth2User oAuth2User) {
        if ("google".equals(provider)) {
            log.info("Google OAuth login");
            return new GoogleUserDetails(oAuth2User.getAttributes());
        }

        throw new OAuth2AuthenticationException("Unsupported OAuth provider: " + provider);
    }

    private Member createOAuthMember(String userId, String userName, String provider, String providerId) {
        Member member = Member.builder()
                .userId(userId)
                .userName(userName)
                .password("")
                .provider(provider)
                .providerId(providerId)
                .role(RoleType.USER)
                .build();

        memberRepository.create(member);
        return member;
    }
}
