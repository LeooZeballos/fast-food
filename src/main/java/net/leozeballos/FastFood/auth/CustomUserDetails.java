package net.leozeballos.FastFood.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final Long branchId;

    public CustomUserDetails(String username, String password, boolean enabled, 
                             Collection<? extends GrantedAuthority> authorities, Long branchId) {
        super(username, password, enabled, true, true, true, authorities);
        this.branchId = branchId;
    }
}
