package org.springshop.user_service.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "user_profile")
public class UserProfile {
    @Id
    private Integer id;
    @Column(name = "firstname", nullable = true)
    private String firstName;
    @Column(name = "lastname", nullable = true)
    private String lastName;
    @Column(name = "profile_picture_url", nullable = true)
    private String profilePicUrl;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    User user;
}
