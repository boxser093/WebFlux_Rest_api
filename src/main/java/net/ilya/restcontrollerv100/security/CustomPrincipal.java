package net.ilya.restcontrollerv100.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.entity.UserRole;

import java.security.Principal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPrincipal implements Principal {
    public Long id;
    private String name;
}
