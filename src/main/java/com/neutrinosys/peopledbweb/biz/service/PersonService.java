package com.neutrinosys.peopledbweb.biz.service;

import com.neutrinosys.peopledbweb.biz.model.Person;
import com.neutrinosys.peopledbweb.data.FileStorageRepository;
import com.neutrinosys.peopledbweb.data.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipInputStream;


@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final FileStorageRepository fileStorageRepository;

    public PersonService(PersonRepository personRepository, FileStorageRepository fileStorageRepository) {
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
    }
   // @Transactional
    public  Person save(Person person, InputStream inputStream) {
        Person person1 = personRepository.save(person);
        fileStorageRepository.save(person1.getFolderFileName(), inputStream);
        return person1;
    }

    public Iterable<Person> findAll() {
        return personRepository.findAll();
    }

    public Page<Person> findAll(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    public Optional<Person> findById(Long aLong) {
        return personRepository.findById(aLong);
    }

    public void deleteAllById(Iterable< Long> longs) {
        /*Iterable<Person> peopleToDelete = p.findAllById(longs);
        Stream<Person> peopleStream = StreamSupport.stream(peopleToDelete.spliterator(), false);
        Set<String> filenames = peopleStream
                .map(Person::getFolderFileName)
                .collect(Collectors.toSet());
        */
        Set<String> filenames = personRepository.findFileNamesById(longs);
        personRepository.deleteAllById(longs);
        fileStorageRepository.deleteAllByName(filenames);
    }

    public void importCSV(InputStream csvFileStream) {

        try {
            ZipInputStream zipInputStream = new ZipInputStream(csvFileStream);
            zipInputStream.getNextEntry();
            InputStreamReader inputStreamReader = new InputStreamReader(zipInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            bufferedReader
                    .lines()
                    .skip(1)
                    .limit(100)
                    .map(Person::parse)
                 .forEach(personRepository::save);
            ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
