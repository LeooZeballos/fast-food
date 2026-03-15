package net.leozeballos.FastFood.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditService {

    /**
     * Log a security or business critical action.
     * @param action Description of the action (e.g., "DELETE_PRODUCT")
     * @param details Additional context (e.g., "ID=42, Name=Burger")
     */
    public void logAction(String action, String details) {
        String username = SecurityContextHolder.getContext().getAuthentication() != null 
                ? SecurityContextHolder.getContext().getAuthentication().getName() 
                : "SYSTEM";
        
        log.info("AUDIT | User: {} | Action: {} | Details: {}", username, action, details);
    }
}
