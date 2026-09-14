package com.honeybudget.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * [자산 역할/용도 구분]
 * 문자열 대신 타입 세이프한 Enum을 사용하여 정합성을 보장합니다.
 */
@Getter
@RequiredArgsConstructor
public enum AssetRole {
    EXPENSE("지출용"),
    INVESTMENT("투자용"),
    SAVINGS("저축용"),
    RESERVE("비상금용");

    private final String description;
}
