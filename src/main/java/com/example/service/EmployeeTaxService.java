package com.example.service;

import com.example.entity.EmployeeTaxDetails;
import com.example.repository.EmployeeTaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeTaxService {

    @Autowired
    private EmployeeTaxRepository employeeTaxRepository;

    public List<EmployeeTaxDetails> calculateTaxForCurrentFinancialYear() {
        List<EmployeeTaxDetails> taxDetailsList = employeeTaxRepository.findAll();
        for (EmployeeTaxDetails taxDetails : taxDetailsList) {
            BigDecimal yearlySalary = taxDetails.getYearlySalary();
            BigDecimal taxAmount = calculateTaxAmount(yearlySalary);
            BigDecimal cessAmount = calculateCess(yearlySalary);
            taxDetails.setTaxAmount(taxAmount);
            taxDetails.setCessAmount(cessAmount);
        }

        return taxDetailsList;
    }

    public EmployeeTaxDetails getEmployeeTaxById(Long id) {
        return employeeTaxRepository.findById(id).orElse(null);
    }

    public List<EmployeeTaxDetails> getAllEmployeeTaxes() {
        return employeeTaxRepository.findAll();
    }

    public EmployeeTaxDetails createEmployeeTax(EmployeeTaxDetails taxDetails) {
        validateTaxDetails(taxDetails);

        BigDecimal taxAmount = calculateTaxAmount(taxDetails.getYearlySalary());
        BigDecimal cessAmount = calculateCess(taxDetails.getYearlySalary());
        taxDetails.setTaxAmount(taxAmount);
        taxDetails.setCessAmount(cessAmount);

        return employeeTaxRepository.save(taxDetails);
    }

    public EmployeeTaxDetails updateEmployeeTax(Long id, EmployeeTaxDetails updatedDetails) {
        validateTaxDetails(updatedDetails);

        Optional<EmployeeTaxDetails> existingTaxDetailsOptional = employeeTaxRepository.findById(id);
        if (existingTaxDetailsOptional.isPresent()) {
            EmployeeTaxDetails existingTaxDetails = existingTaxDetailsOptional.get();
            updateTaxDetails(existingTaxDetails, updatedDetails);
            return employeeTaxRepository.save(existingTaxDetails);
        }
        return null;
    }

    public void deleteEmployeeTax(Long id) {
        employeeTaxRepository.deleteById(id);
    }

    private void updateTaxDetails(EmployeeTaxDetails existingTaxDetails, EmployeeTaxDetails updatedDetails) {
        existingTaxDetails.setEmployeeCode(updatedDetails.getEmployeeCode());
        existingTaxDetails.setFirstName(updatedDetails.getFirstName());
        existingTaxDetails.setLastName(updatedDetails.getLastName());
        existingTaxDetails.setYearlySalary(updatedDetails.getYearlySalary());
        existingTaxDetails.setTaxAmount(calculateTaxAmount(updatedDetails.getYearlySalary()));
        existingTaxDetails.setCessAmount(calculateCess(updatedDetails.getYearlySalary()));
    }

    private BigDecimal calculateTaxAmount(BigDecimal yearlySalary) {
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (yearlySalary.compareTo(BigDecimal.valueOf(250000)) > 0) {
            if (yearlySalary.compareTo(BigDecimal.valueOf(500000)) <= 0) {
                taxAmount = yearlySalary.subtract(BigDecimal.valueOf(250000)).multiply(BigDecimal.valueOf(0.05));
            } else if (yearlySalary.compareTo(BigDecimal.valueOf(1000000)) <= 0) {
                taxAmount = BigDecimal.valueOf(12500).add(yearlySalary.subtract(BigDecimal.valueOf(500000)).multiply(BigDecimal.valueOf(0.1)));
            } else {
                taxAmount = BigDecimal.valueOf(62500).add(yearlySalary.subtract(BigDecimal.valueOf(1000000)).multiply(BigDecimal.valueOf(0.2)));
            }
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

    private void validateTaxDetails(EmployeeTaxDetails taxDetails) {
        if (taxDetails == null || taxDetails.getEmployeeCode() == null || taxDetails.getEmployeeCode().isEmpty()) {
            throw new IllegalArgumentException("Employee tax details are invalid.");
        }
    }
}
