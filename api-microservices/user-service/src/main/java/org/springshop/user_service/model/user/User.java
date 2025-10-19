package org.springshop.user_service.model.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;
    @Column(name = "sub", unique = true)
    private String sub;
    @Column(name = "username", unique=true)
    private String username;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    UserProfile profile;
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
