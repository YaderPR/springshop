package org.transportracker.api.dto.user;

import java.util.Set;

public class UserDTO {
    private Long id;
    private String keycloakUserId;
    private String email;
    private String fullName;
    private Boolean status;
    private Set<RoleDTO> roles;

    public UserDTO() { }

    public UserDTO(Long id, String keycloakUserId, String email, String fullName, Boolean status, Set<RoleDTO> roles) {
        this.id = id;
        this.keycloakUserId = keycloakUserId;
        this.email = email;
        this.fullName = fullName;
        this.status = status;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKeycloakUserId() { return keycloakUserId; }
    public void setKeycloakUserId(String keycloakUserId) { this.keycloakUserId = keycloakUserId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public Set<RoleDTO> getRoles() { return roles; }
    public void setRoles(Set<RoleDTO> roles) { this.roles = roles; }
}

