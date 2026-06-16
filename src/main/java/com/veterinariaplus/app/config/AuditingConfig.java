package com.veterinariaplus.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita la auditoria automatica de JPA para que los campos
 * created_at y updated_at de EntidadBase se completen solos.
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig {
}
