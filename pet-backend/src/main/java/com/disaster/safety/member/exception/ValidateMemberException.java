package com.disaster.safety.member.exception;

// 회원가입 시 이미 존재하는 회원을 검증할 때 발생하는 예외
public class ValidateMemberException extends RuntimeException {

    public ValidateMemberException(String message) {
        super(message);
    }
}
