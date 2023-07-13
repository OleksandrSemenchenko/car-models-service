package ua.com.foxminded.vehicles.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    VEHICLE_CREATE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to save the vehicle data"),
    VEHICLE_FETCH_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to fetch the vehicle data"),
    VEHICLE_UPDATE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to update the vehicle data"),
    VEHICLE_DELETE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to delete the vehicle data"),
    VEHICLE_ABSENCE(BAD_REQUEST.value(), "The specified model is not present"),
    
    MODEL_CREATE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to save the model data"),
    MODEL_FETCH_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to fetch the model data"),
    MODEL_UPDATE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to update the model data"),
    MODEL_DELETE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to delete the model data"),
    MODEL_ABSENCE(BAD_REQUEST.value(), "The specified model is not present"),
    
    CATEGORY_CREATE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to save the category data"),
    CATEGORY_FETCH_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to fetch category data"),
    CATEGORY_UPDATE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to update category data"),
    CATEGORY_DELETE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to delete category data"),
    CATEGORY_ABSENCE(BAD_REQUEST.value(), "The specified manufacturer is not present"),
    
    MANUFACTURER_CREATE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to save the manufacturer data"), 
    MANUFACTURER_FETCH_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to fetch the manufacturer data"), 
    MANUFACTURER_UPDATE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to update the manufacturer data"),
    MANUFACTURER_DELETE_ERROR(INTERNAL_SERVER_ERROR.value(), "Failed to delete the manufacturer data"),
    MANUFACTURER_ABSENCE(BAD_REQUEST.value(), "The specified manufacturer is not present");
    
    private final int code;
    private final String description;
}
