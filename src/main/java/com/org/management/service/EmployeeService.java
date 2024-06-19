package com.org.management.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.org.management.model.Employee;
import com.org.management.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	public boolean initialSave(MultipartFile file) throws IOException {
		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
			List<String[]> lines = reader.readAll();
			for (String[] line : lines) {
				if (line[0].equals("EmployeeID"))
					continue;
				
	            if (line.length < 3) {
	                throw new IOException("Invalid CSV format: Insufficient data");
	            }

				Employee employee = Employee
									.builder()
									.employeeId(Integer.parseInt(line[0]))
									.name(line[1])
									.title(line[2])
									.build();

				// Edge case: CEO does not report to anyone
				if (line[3] != "") {
					employee.setManagerId(Integer.parseInt(line[3]));
				}

				employeeRepository.initialSave(employee);
			}
		} catch (IOException | CsvException e) {
			throw new IOException("Error saving employees to repository", e);
		}
		return true;
	}
	
	public int save(Employee employee) {
		return employeeRepository.save(employee);
	}

	public List<Employee> getEmployees() {
		return employeeRepository.getEmployees();
	}

	public Employee getEmployee(Integer employeeId) {
		return employeeRepository.getEmployee(employeeId);
	}

	public Employee getManager(Employee employee) {
		return employeeRepository.getManager(employee);
	}

	public List<Employee> getSubordinates(Employee employee) {
		return employeeRepository.getSubordinates(employee);
	}
}
