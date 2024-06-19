package com.org.management.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.management.model.Employee;
import com.org.management.service.EmployeeService;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(EmployeeController.class)
@DisplayName("Employee Controller Test")
public class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EmployeeService employeeService;
	
    @Autowired
    private ObjectMapper objectMapper;

	private Employee employee;

	@BeforeEach
	public void setUp() {
		employee = Employee
					.builder()
					.employeeId(77)
					.name("John Doe")
					.title("Software Engineer")
					.managerId(4)
					.build();
	}
	
    @Test
    @DisplayName("Save employees from non-empty csv")
    public void testLoadData_Success() throws Exception {
    	// Arrange
    	MockMultipartFile mockFile = new MockMultipartFile(
							            "employees", 
							            "test.csv", 
							            MediaType.TEXT_PLAIN_VALUE,
							            "EmployeeId,Name,Title,ManagerId\n1,John Doe,CEO,\n2,Jane Smith,CTO,1".getBytes()
							        );
    	
    	// Stub
        when(employeeService.initialSave((MultipartFile) any(MultipartFile.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(multipart("/employees/startup")
                .file(mockFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(content().string("Data loaded successfully"));
    }
	
	@Test
	@DisplayName("Save employees from empty csv")
	public void testLoadData_Empty() throws Exception {
		// Arrange
    	MockMultipartFile mockFile = new MockMultipartFile(
							            "employees", 
							            "test.csv", 
							            MediaType.TEXT_PLAIN_VALUE,
							            "".getBytes()
							        );
    	
    	// Act & Assert
    	mockMvc.perform(multipart("/employees/startup")
    			.file(mockFile)
    			.contentType(MediaType.MULTIPART_FORM_DATA))
		    	.andExpect(status().isBadRequest())
		    	.andExpect(content().string("File is empty"));
	}
	
	@Test
	@DisplayName("Save employees from invalid csv")
	public void testLoadData_InvalidFormat() throws Exception {
		// Arrange
    	MockMultipartFile mockFile = new MockMultipartFile(
	            "employees", 
	            "test.csv", 
	            MediaType.TEXT_PLAIN_VALUE,
	            "EmployeeId,Name,Title,ManagerId\n1,CEO,\n2,Jane Smith".getBytes()
	        );
    	
    	// Stub
		when(employeeService.initialSave((MultipartFile) any(MultipartFile.class)))
			.thenThrow(new IOException("Invalid CSV format: Insufficient data"));
		
		// Act & Assert
		mockMvc.perform(multipart("/employees/startup")
				.file(mockFile)
				.contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string("Error processing file: Invalid CSV format: Insufficient data"));
	}
	
	@Test
	@DisplayName("Get employees from non-empty list")
	public void testGetEmployees_Success() throws Exception {
	    // Arrange
	    List<Employee> employees = new ArrayList<>();
	    employees.add(new Employee(1, "John Doe", "Engineering", 2));
	    employees.add(new Employee(2, "Andy Rivera", "Marketing", 1));
	    
	    // Stub
	    given(employeeService.getEmployees()).willReturn(employees);
	    
	    // Act & Assert
	    mockMvc.perform(get("/employees")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(content().json(objectMapper.writeValueAsString(employees)));
		
	}
	
	@Test
	@DisplayName("Get employees from empty list")
	public void testGetEmployees_Empty() throws Exception {
		// Arrange
	    List<Employee> employees = new ArrayList<>();
	    
	    // Stub
		given(employeeService.getEmployees()).willReturn(employees);
		
		// Act & Assert
		mockMvc.perform(get("/employees")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("No employees exist"));
	}
	
	@Test
	@DisplayName("Get existing employee by id")
	public void testGetEmployee_Success() throws Exception {
		// Stub
		given(employeeService.getEmployee(77)).willReturn(employee);
		
		// Act & Assert
        mockMvc.perform(get("/employees/77")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(77)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.title", is("Software Engineer")))
                .andExpect(jsonPath("$.managerId", is(4)));
	}
	
	@Test
	@DisplayName("Get non-existing employee by id")
	public void testGetEmployee_NotFound() throws Exception {
		// Stub
		given(employeeService.getEmployee(77)).willReturn(employee);
		
		// Act & Assert
        mockMvc.perform(get("/employees/76")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("EmployeeID does not exist"));
	}
	
	
}
