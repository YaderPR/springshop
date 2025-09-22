package org.springshop.api.model.payment;

import org.springshop.api.model.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    private String phoneNumber; // opcional, para contacto de envío

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // dueño de la dirección
}

