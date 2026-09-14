package com.honeybudget.domain.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.YearMonth;

/**
 * YearMonth 타입을 데이터베이스 VARCHAR(7) 형식("YYYY-MM")으로 상호 변환하는 JPA 컨버터.
 * autoApply = true 로 지정되어 모든 엔티티의 YearMonth 필드에 자동 적용됩니다.
 */
@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, String> {

    @Override
    public String convertToDatabaseColumn(YearMonth attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public YearMonth convertToEntityAttribute(String dbData) {
        return dbData != null ? YearMonth.parse(dbData) : null;
    }
}
