package com.org.management.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(EmployeeController.class)
@DisplayName("Employee Controller Tests")
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
	
	@Nested
	@DisplayName("Startup Endpoint Tests")
	public class LoadDataTests {
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
	}
	
	@Nested
	@DisplayName("Get Employees Endpoint Tests")
	public class GetEmployeesTests {
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
	}

	@Nested
	@DisplayName("Get Employee Endpoint Tests")
	public class GetEmployeeTests {
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
	
	@Nested
	@DisplayName("Get Manager Endpoint Tests")
	public class GetManagerTests {
		@Test
		@DisplayName("Get manager for non-existing employee id")
		public void testGetManager_EmployeeNotFound() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/employees/76/manager")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest())
					.andExpect(content().string("EmployeeID does not exist"));
		}
		
		@Test
		@DisplayName("Get manager for CEO employee id")
		public void testGetManager_ManagerNotFound() throws Exception {
			// Stub
			given(employeeService.getEmployee(77)).willReturn(employee);
			given(employeeService.getManager(employee)).willThrow(new IOException());
		
			// Act & Assert
			mockMvc.perform(get("/employees/77/manager")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest())
					.andExpect(content().string("CEO does not have a manager"));
		}
		
		@Test
		@DisplayName("Get non-existing manager for employee id")
		public void testGetManager_IllegalState() throws Exception {
			// Stub
			given(employeeService.getEmployee(77)).willReturn(employee);
			given(employeeService.getManager(employee)).willThrow(new IllegalStateException());
		
			// Act & Assert
			mockMvc.perform(get("/employees/77/manager")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isConflict())
					.andExpect(content().string("Employee is missing manager"));
		}
		
		@Test
		@DisplayName("Get existing manager for employee id")
		public void testGetManager_Success() throws Exception {
			// Arrange
			Employee mockManager = Employee
									.builder()
									.employeeId(4)
									.name("Andy Rivera")
									.title("Senior Engineer")
									.managerId(0)
									.build();
			
			// Stub
			given(employeeService.getEmployee(77)).willReturn(employee);
			given(employeeService.getManager(employee)).willReturn(mockManager);
			
			// Act & Assert
			mockMvc.perform(get("/employees/77/manager")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
			        .andExpect(jsonPath("$.employeeId", is(4)))
			        .andExpect(jsonPath("$.name", is("Andy Rivera")))
			        .andExpect(jsonPath("$.title", is("Senior Engineer")))
			        .andExpect(jsonPath("$.managerId", is(0)));
			
		}	
	}

	@Nested
	@DisplayName("Get Subordinates Endpoint Tests")
	public class GetSubordinatesTests {
		
		@Test
		@DisplayName("Get subordinates for non-existing employee id")
		public void testGetSubordinates_EmployeeNotFound() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/employees/76/subordinates")
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("EmployeeID does not exist"));
		}
		
		@Test
		@DisplayName("Get non-existing subordinates for employee id")
		public void testGetSubordinates_SubordinatesNotFound() throws Exception {
			// Stub
			given(employeeService.getEmployee(77)).willReturn(employee);
			
			// Act & Assert
			mockMvc.perform(get("/employees/77/subordinates")
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(content().string("Employee has no subordinates"));
		}
		
		@Test
		@DisplayName("Get subordinates for employee id")
		public void testGetSubordinates_Success() throws Exception {
		    // Arrange
		    List<Employee> subordinates = new ArrayList<>();
		    subordinates.add(new Employee(1, "John Doe", "Engineering", 77));
		    subordinates.add(new Employee(2, "Andy Rivera", "Marketing", 77));
		    
		    // Stub
		    given(employeeService.getEmployee(77)).willReturn(employee);
		    given(employeeService.getSubordinates(employee)).willReturn(subordinates);
		    
		    // Act & Assert
		    mockMvc.perform(get("/employees/77/subordinates")
		    		.contentType(MediaType.APPLICATION_JSON))
		    .andExpect(status().isOk())
		    .andExpect(content().json(objectMapper.writeValueAsString(subordinates)));
			
		}
	}
	
	@Nested
	@DisplayName("Add Employee Endpoint Tests")
	public class AddEmployeeTests {
		
		@Test
		@DisplayName("Add employee that already exists")
		public void testAddEmployee_EmployeeExists() throws Exception {
		    // Arrange
		    String employeeJson = objectMapper.writeValueAsString(employee);
		    
		    // Stub
 			given(employeeService.save(employee)).willReturn(-1);
			
 			// Act & Assert
			mockMvc.perform(post("/employees")
					.contentType(MediaType.APPLICATION_JSON)
					.content(employeeJson))
					.andExpect(status().isConflict())
					.andExpect(content().string("Employee already exists"));
		}
		
		@Test
		@DisplayName("Add employee with manager that does not exist")
		public void testAddEmployee_InvalidManager() throws Exception {
			// Arrange
			String employeeJson = objectMapper.writeValueAsString(employee);
			
			// Stub
			given(employeeService.save(employee)).willReturn(0);
			
			// Act & Assert
			mockMvc.perform(post("/employees")
					.contentType(MediaType.APPLICATION_JSON)
					.content(employeeJson))
					.andExpect(status().isConflict())
					.andExpect(content().string("Manager does not exist"));
		}
		
		@Test
		@DisplayName("Add employee that does not exist")
		public void testAddEmployee_Success() throws Exception {
			// Arrange
			String employeeJson = objectMapper.writeValueAsString(employee);
			
			// Stub
			given(employeeService.save(employee)).willReturn(1);
			
			// Act & Assert
			mockMvc.perform(post("/employees")
					.contentType(MediaType.APPLICATION_JSON)
					.content(employeeJson))
			.andExpect(status().isOk())
			.andExpect(content().json(employeeJson));
		}
	}
}
