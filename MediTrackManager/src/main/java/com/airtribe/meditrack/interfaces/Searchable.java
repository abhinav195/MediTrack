package com.airtribe.meditrack.interfaces;

import com.airtribe.meditrack.entity.Person;
import com.airtribe.meditrack.exception.PersonNotFoundException;

public interface Searchable {
    public Person SearchByName(String name) throws PersonNotFoundException;
    public Person SearchByAge(int age) throws PersonNotFoundException;
    public Person SearchById(String id) throws PersonNotFoundException;
}
