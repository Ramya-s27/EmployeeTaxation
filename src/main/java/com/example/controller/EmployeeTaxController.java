package com.example.controller;

import com.example.entity.EmployeeTaxDetails;
import com.example.service.EmployeeTaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employee-tax")
public class EmployeeTaxController {

    @Autowired
    private EmployeeTaxService employeeTaxService;

    @PostMapping("/calculate")
    public ResponseEntity<List<EmployeeTaxDetails>> calculateTaxForEmployees() {
        List<EmployeeTaxDetails> taxDetailsList = employeeTaxService.calculateTaxForCurrentFinancialYear();
        return new ResponseEntity<>(taxDetailsList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeTaxDetails> getEmployeeTaxById(@PathVariable Long id) {
        EmployeeTaxDetails taxDetails = employeeTaxService.getEmployeeTaxById(id);
        return taxDetails != null ?
                new ResponseEntity<>(taxDetails, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeTaxDetails>> getAllEmployeeTaxes() {
        List<EmployeeTaxDetails> taxDetailsList = employeeTaxService.getAllEmployeeTaxes();
        return new ResponseEntity<>(taxDetailsList, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeTaxDetails> createEmployeeTax(@Valid @RequestBody EmployeeTaxDetails taxDetails) {
        EmployeeTaxDetails createdTaxDetails = employeeTaxService.createEmployeeTax(taxDetails);
        return new ResponseEntity<>(createdTaxDetails, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeTaxDetails> updateEmployeeTax(@PathVariable Long id, @Valid @RequestBody EmployeeTaxDetails taxDetails) {
        EmployeeTaxDetails updatedTaxDetails = employeeTaxService.updateEmployeeTax(id, taxDetails);
        if (updatedTaxDetails != null) {
            return new ResponseEntity<>(updatedTaxDetails, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployeeTax(@PathVariable Long id) {
        employeeTaxService.deleteEmployeeTax(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
