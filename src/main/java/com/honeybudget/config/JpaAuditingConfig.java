package com.honeybudget.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 설정.
 * 슬라이스 테스트 시 메타모델 로딩 오류를 방지하기 위해 메인 애플리케이션과 분리한다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
