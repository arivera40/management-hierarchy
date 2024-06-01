package com.org.management.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.org.management.model.Employee;
import com.org.management.service.EmployeeService;

import jakarta.validation.Valid;

@RestController
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@PostMapping("/employees/startup")
	public ResponseEntity<?> loadData(@RequestParam("employees") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
		}

		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
			List<String[]> lines = reader.readAll();
			for (String[] line : lines) {
				if (line[0].equals("EmployeeID"))
					continue;

				Employee employee = Employee.builder().employeeId(Integer.parseInt(line[0])).name(line[1])
						.title(line[2]).build();

				// Edge case: CEO does not report to anyone
				if (line[3] != "") {
					employee.setManagerId(Integer.parseInt(line[3]));
				}

				employeeService.initialSave(employee);
			}
		} catch (IOException | CsvException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing file: " + e.getMessage());
		}

		return ResponseEntity.status(HttpStatus.OK).body("Data loaded successfully");
	}

	@RequestMapping("/employees")
	public ResponseEntity<?> getEmployees() {
		List<Employee> employees = employeeService.getEmployees();

		// Check if employees exist
		if (employees.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No employees exist");
		}
		return ResponseEntity.status(HttpStatus.OK).body(employees);
	}

	@RequestMapping("/employees/{id}")
	public ResponseEntity<?> getEmployee(@PathVariable Integer id) {
		Employee employee = employeeService.getEmployee(id);

		// Check if employee exists
		if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EmployeeID does not exist");
		}
		return ResponseEntity.status(HttpStatus.OK).body(employee);
	}

	@RequestMapping("/employees/{id}/manager")
	public ResponseEntity<?> getManager(@PathVariable Integer id) {
		Employee employee = employeeService.getEmployee(id);

		// Check if employee exists
		if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EmployeeID does not exist");
		}

		Employee manager = employeeService.getManager(employee);

		// Check if manager exists
		if (manager == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CEO does not have a manager");
		}
		return ResponseEntity.status(HttpStatus.OK).body(manager);
	}

	@RequestMapping("/employees/{id}/subordinates")
	public ResponseEntity<?> getSubordinates(@PathVariable Integer id) {
		Employee employee = employeeService.getEmployee(id);

		// Check if employee exists
		if (employee == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EmployeeID does not exist");
		}

		List<Employee> subordinates = employeeService.getSubordinates(employee);
		// Check if manager exists
		if (subordinates.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee has no subordinates");
		}
		return ResponseEntity.status(HttpStatus.OK).body(subordinates);
	}
	
	@PostMapping("/employees")
	public ResponseEntity<?> addEmployee(@Valid @RequestBody Employee employee) {
		int status = employeeService.save(employee);
		
		if (status == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Employee already exists");
		} else if (status == 0) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Manager does not exist");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(employee);
	}
//	
//	@PutMapping("/employees/{id}")
//	public ResponseEntity<?> updateEmployee(@RequestParam Integer employeeId, @RequestBody EmployeeObject employeeObj) {
//		
//	}
//	
//	@DeleteMapping("/employees/{id}")
//	public ResponseEntity<?> deleteEmployee(@RequestParam Integer employeeId) {
//		
//	}
}
