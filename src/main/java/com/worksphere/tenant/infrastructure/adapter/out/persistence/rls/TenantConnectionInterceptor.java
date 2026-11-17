package com.worksphere.tenant.infrastructure.adapter.out.persistence.rls;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.PreparedStatement;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class TenantConnectionInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TenantConnectionInterceptor.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Before("within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Repository *)")
    public void setPostgresTenantContext() {
        String tenantId = TenantContextHolder.getTenantId();
        try {
            Session session = entityManager.unwrap(Session.class);
            session.doWork(connection -> {
                try (PreparedStatement ps = connection.prepareStatement("SELECT set_config('app.current_tenant_id', ?, true)")) {
                    ps.setString(1, tenantId);
                    ps.execute();
                    log.debug("Enforced PostgreSQL RLS app.current_tenant_id = '{}'", tenantId);
                }
            });
        } catch (Exception e) {
            log.warn("Could not set PostgreSQL RLS tenant context (might be outside active session): {}", e.getMessage());
        }
    }
}
