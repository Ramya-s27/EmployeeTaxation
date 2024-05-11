package com.example.service;

import com.example.entity.Employee;
import com.example.entity.EmployeeTaxDetails;
import com.example.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee addEmployee(@Valid Employee employee) {
        validateEmployee(employee);
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<EmployeeTaxDetails> calculateTax() {
        List<Employee> employees = getAllEmployees();
        List<EmployeeTaxDetails> taxDetailsList = new ArrayList<>();

        for (Employee employee : employees) {
            BigDecimal yearlySalary = calculateYearlySalary(employee.getDoj(), employee.getSalary());
            BigDecimal taxAmount = calculateTaxAmount(yearlySalary);
            BigDecimal cessAmount = calculateCess(yearlySalary);

            EmployeeTaxDetails ed = new EmployeeTaxDetails();
            ed.setEmployeeCode(employee.getEmployeeId());
            ed.setFirstName(employee.getFirstName());
            ed.setLastName(employee.getLastName());
            ed.setYearlySalary(BigDecimal.valueOf(yearlySalary.doubleValue()));
            ed.setTaxAmount(BigDecimal.valueOf(taxAmount.doubleValue()));
            ed.setCessAmount(BigDecimal.valueOf(cessAmount.doubleValue()));

            taxDetailsList.add(ed);
        }

        return taxDetailsList;
    }

    private BigDecimal calculateYearlySalary(LocalDate doj, BigDecimal salaryPerMonth) {
        LocalDate currentDate = LocalDate.now();
        int numberOfMonths = 12 - doj.getMonthValue() + currentDate.getMonthValue() + (currentDate.getYear() - doj.getYear() - 1) * 12;
        return salaryPerMonth.multiply(BigDecimal.valueOf(numberOfMonths));
    }

    private BigDecimal calculateTaxAmount(BigDecimal yearlySalary) {
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (yearlySalary.compareTo(BigDecimal.valueOf(250000)) <= 0) {
            taxAmount = BigDecimal.ZERO;
        } else if (yearlySalary.compareTo(BigDecimal.valueOf(500000)) <= 0) {
            taxAmount = yearlySalary.subtract(BigDecimal.valueOf(250000)).multiply(BigDecimal.valueOf(0.05));
        } else if (yearlySalary.compareTo(BigDecimal.valueOf(1000000)) <= 0) {
            taxAmount = BigDecimal.valueOf(12500).add(yearlySalary.subtract(BigDecimal.valueOf(500000)).multiply(BigDecimal.valueOf(0.1)));
        } else {
            taxAmount = BigDecimal.valueOf(62500).add(yearlySalary.subtract(BigDecimal.valueOf(1000000)).multiply(BigDecimal.valueOf(0.2)));
        }
        return taxAmount;
    }

    private BigDecimal calculateCess(BigDecimal yearlySalary) {
        BigDecimal cessAmount = BigDecimal.ZERO;
        if (yearlySalary.compareTo(BigDecimal.valueOf(2500000)) > 0) {
            cessAmount = yearlySalary.subtract(BigDecimal.valueOf(2500000)).multiply(BigDecimal.valueOf(0.02));
        }
        return cessAmount;
    }

    private void validateEmployee(Employee employee) {
        if (employee.getEmployeeId() == null || employee.getEmployeeId().isEmpty() ||
                employee.getFirstName() == null || employee.getFirstName().isEmpty() ||
                employee.getLastName() == null || employee.getLastName().isEmpty() ||
                employee.getEmail() == null || employee.getEmail().isEmpty() ||
                employee.getPhoneNumbers() == null || employee.getPhoneNumbers().isEmpty() ||
                employee.getDoj() == null ||
                employee.getSalary() == null || employee.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("All fields are mandatory and salary must be greater than zero.");
        }

        if (!isValidEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }
}
