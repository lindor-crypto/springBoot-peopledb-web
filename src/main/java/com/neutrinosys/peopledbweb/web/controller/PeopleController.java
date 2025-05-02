package com.neutrinosys.peopledbweb.web.controller;

import com.neutrinosys.peopledbweb.biz.model.Person;
import com.neutrinosys.peopledbweb.biz.service.PersonService;
import com.neutrinosys.peopledbweb.data.FileStorageRepository;
import com.neutrinosys.peopledbweb.data.PersonRepository;
import com.neutrinosys.peopledbweb.exception.StorageException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
@Controller
@RequestMapping("/people")
@Log4j2
public class PeopleController {

    public static final String DISPO = """
                            
             attachment; filename=""%s
            """;
    private PersonRepository personRepository;
    private PersonService personService;
    private FileStorageRepository fileStorageRepository;

    public PeopleController(PersonRepository personRepository, FileStorageRepository fileStorageRepository,  PersonService personService) {
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
        this.personService = personService;
    }

    /**
     * retourne data from database to people.html
     * @return
     */
    @ModelAttribute("people")
    public Page<Person> getPeople(@PageableDefault(size = 20) Pageable page){
        return  personService.findAll(page);
    }

    /**
     * Populate a person which declared in people.html "${person}"
     * @return
     */
    @ModelAttribute
    public Person getPerson(){
        return new Person();
    }


    /**
     * this refers to people.html located in templates folder
     * @param model
     * @return
     */
    @GetMapping
    public String showPeoplePage(Model model){
        //model.addAttribute("people", getPeople());
        return "people";
    }
    @GetMapping("/images/{resource}")
    public ResponseEntity<Resource> getResource(@PathVariable String resource){
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, format(DISPO, resource))
                .body(fileStorageRepository.findByName(resource));
    }
    @PostMapping
    public String SavePerson(Model model, @Valid Person person, Errors errors, @RequestParam("folderFileName") MultipartFile photoFile) throws IOException {
        log.info(person);
        if (!errors.hasErrors()) {

            try {
                personService.save(person, photoFile.getInputStream());
                return "redirect:people";
            } catch (StorageException e) {
                model.addAttribute("errormsg", "Something wrong during saving person ");
                return "people";
            }
        }
        return "people";
    }

    /**
     *         if (selections.isPresent()) {
     *             personRepository.deleteAllById(selections.get());
     *         }
     * @param selections
     * @return
     */
    @PostMapping(params = "action=delete")
    public String deletePerson(@RequestParam Optional<Set<Long>> selections){
        selections.ifPresent(longs -> personService.deleteAllById(longs));
        return "redirect:people";
    }

    /**
     *
     * @param selections
     * @param model
     * @return
     */
    @PostMapping(params = "action=edit")
    public String updatePerson(@RequestParam Optional<Set<Long>> selections, Model model){
       log.info(selections);
        if (selections.isPresent()) {
            Optional<Person> person = personRepository.findById(selections.get().iterator().next());
            model.addAttribute("person", person);
        }
        return "people";
    }
    @PostMapping(params = "action=import")
    public String importCSV( @RequestParam MultipartFile csvFile){
       log.info("File name :" +csvFile.getOriginalFilename());
       log.info("File size :" +csvFile.getSize());
        try {
            personService.importCSV(csvFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:people";
    }
}

