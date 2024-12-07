package com.systems.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequest {
    private int page;    
    private int size;   
    private String sortBy; 
    private String sortDirection;
}
