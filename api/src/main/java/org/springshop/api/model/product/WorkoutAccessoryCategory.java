package org.springshop.api.model.product;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "workout_accessories_category")
public class WorkoutAccessoryCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @OneToMany(mappedBy = "workoutAccesoryCategory")
    private Set<WorkoutAccessory> workoutAccessories;
    public WorkoutAccessoryCategory(String name) {
        this.name = name;
    }
}
