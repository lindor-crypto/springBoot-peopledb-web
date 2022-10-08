package com.neutrinosys.peopledbweb.biz.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


class PersonTest {
    @Test
    public  void canParse(){
        String csvLine="James,Butt,Benton, John B Jr,6649 N Blue Gum St,New Orleans,Orleans,LA,70116,504-621-8927,504-845-1427,jbutt@gmail.com,http://www.bentonjohnbjr.com,7/13/1400";
        Person per = Person.parse(csvLine);
        assertThat(per.getFirstname()).isEqualTo("Butt");
        assertThat(per.getLastname()).isEqualTo("James");
        assertThat(per.getEmail()).isEqualTo("jbutt@gmail.com");
    }

}