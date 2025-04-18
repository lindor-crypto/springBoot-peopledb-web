package com.neutrinosys.peopledbweb.biz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PreRemove;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastname;

    @NotEmpty(message = " First name cannot be empty")
    private String firstname;

    @Past(message = "Date of be must be in the past ")
    @NotNull(message = "Date of b must be specified")
    private LocalDate dob;

    @Email(message = "Email name cannot be empty")
    @NotEmpty(message = "email cannot be empty")
    private String email;

    @DecimalMin(value = "1000.00", message = "Salary must be a least 1000.00")
    @NotNull(message = "Salary of b must be specified")
    private BigDecimal salary;

    private String folderFileName;

    public static Person parse(String csvLine) {
    String[] fields = csvLine.split(",\\s*");
    LocalDate dob = LocalDate.parse(fields[13], DateTimeFormatter.ofPattern("M/d/yyyy"));
    return new Person(null, fields[0], fields[1], dob, fields[11], new BigDecimal(120000), null );
    }

}
