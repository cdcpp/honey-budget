package com.honeybudget.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정 클래스.
 * @SpringBootApplication 클래스와 분리하여 @WebMvcTest 등 슬라이스 테스트 시
 * JPA 메타모델 로딩 에러가 발생하는 것을 방지합니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
