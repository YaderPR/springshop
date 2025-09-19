package org.springshop.api.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;
    @Column(name = "sub", unique = true)
    private String sub;
    @Column(name = "username", nullable = false, length = 100, unique = true)
    private String username;
    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;
    @Column(name = "role", nullable = false, length = 50)
    private String role;
    @Column(name = "first_name", length = 100)
    private String firstName;
    @Column(name = "last_name", length = 100)
    private String lastName;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return sub != null && user.sub != null && user.sub.equals(sub);
    }
    @Override
    public int hashCode() {
        return sub != null ? sub.hashCode() : 0;
    }
}
