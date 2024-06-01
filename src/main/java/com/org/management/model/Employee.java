package com.org.management.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
	
	@NotNull(message = "Employee ID is required")
	private Integer employeeId;
	
	@NotEmpty(message = "Name is required")
	private String name;
	
	@NotEmpty(message = "Title is required")
	private String title;

	private Integer managerId;
}
