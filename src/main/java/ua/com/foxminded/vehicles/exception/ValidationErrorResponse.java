package ua.com.foxminded.vehicles.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorResponse {
    
    private Instant timestamp;
    private int status;
    private String error;
    private String path;
    
    @Builder.Default
    private List<Violation> violations = new ArrayList<>();
}
