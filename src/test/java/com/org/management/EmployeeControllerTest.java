package com.org.management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.org.management.controller.EmployeeController;
import com.org.management.model.Employee;
import com.org.management.service.EmployeeService;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EmployeeService employeeService;

	private Employee employee;

	@BeforeEach
	public void setUp() {
		employee = Employee.builder().employeeId(77).name("John Doe").title("Software Engineer").managerId(4).build();
	}
	
	@Test
	public void testGetEmployee_Success() throws Exception {
		given(employeeService.getEmployee(77)).willReturn(employee);
		
        mockMvc.perform(get("/employees/77")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(77)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.title", is("Software Engineer")))
                .andExpect(jsonPath("$.managerId", is(4)));
	}
	
	@Test
	public void testGetEmployee_NotFound() throws Exception {
		given(employeeService.getEmployee(77)).willReturn(employee);
		
        mockMvc.perform(get("/employees/76")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("EmployeeID does not exist"));
	}
}
