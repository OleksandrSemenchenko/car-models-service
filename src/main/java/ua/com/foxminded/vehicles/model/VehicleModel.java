package ua.com.foxminded.vehicles.model;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

@Data
public class VehicleModel implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String id;
    private Integer productionYear;
    private ManufacturerModel manufacturer;
    private ModelModel model;
    private Set<CategoryModel> categories;
}
