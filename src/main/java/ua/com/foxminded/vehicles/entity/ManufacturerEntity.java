package ua.com.foxminded.vehicles.entity;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "manufacturers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    private String name;
    
    @OneToMany(mappedBy = "manufacturer")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<VehicleEntity> vhicles;
}
