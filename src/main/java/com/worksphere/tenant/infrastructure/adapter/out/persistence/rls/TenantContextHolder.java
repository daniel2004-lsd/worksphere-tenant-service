package com.worksphere.tenant.infrastructure.adapter.out.persistence.rls;

public final class TenantContextHolder {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    public static final String SYSTEM_TENANT = "SYSTEM";

    private TenantContextHolder() {}

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getTenantId() {
        String tenant = CURRENT_TENANT.get();
        return (tenant != null && !tenant.trim().isEmpty()) ? tenant : SYSTEM_TENANT;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
