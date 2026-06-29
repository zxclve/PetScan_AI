package com.disaster.safety.member.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "MEMBER")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;

    // 유저 이름
    @Column(name = "userName", nullable = false)
    private String userName;

    // 유저 ID
    @Column(name = "userId", unique = true, nullable = false)
    private String userId;

    // 유저 비밀번호
    @Column(name = "password", nullable = false)
    private String password;


	@Enumerated(EnumType.STRING)
	@Column(name = "ROLE", nullable = false)
	private RoleType role;

	// provider는 소셜 로그인 제공자(구글)
	private String provider;
	// providerId는 소셜 로그인 제공자에서 제공하는 고유 ID
	private String providerId;


	public void updatePassword(String password) {
		this.password = password;
	}

	public void updateUserId(String userId) {
		this.userId = userId;
	}
}
