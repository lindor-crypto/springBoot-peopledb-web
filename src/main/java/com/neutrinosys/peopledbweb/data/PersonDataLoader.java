package com.neutrinosys.peopledbweb.data;

import com.neutrinosys.peopledbweb.biz.model.Person;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

//@Component
public class PersonDataLoader implements ApplicationRunner {
    private PersonRepository personRepository;

    public PersonDataLoader(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (personRepository.count()==0) {
            System.out.println("running this stuff here");
            List<Person> people = List.of(
                    new Person(null, "Lindor", "Snake", LocalDate.of(1980, 1, 1),"ld@ld.com", new BigDecimal("50000"), ""),
                    new Person(null, "Sarah", "Smith", LocalDate.of(1970, 2, 2),"ld@ld.com",  new BigDecimal("60000"), ""),
                    new Person(null, "Johnny", "Jackson", LocalDate.of(1990, 3, 3),"ld@ld.com",  new BigDecimal("70000"), ""),
                    new Person(null, "Bobby", "Kim", LocalDate.of(1950, 4, 4),"ld@ld.com",  new BigDecimal("80000"), "")
            );

            personRepository.saveAll(people);
        }
    }
}
