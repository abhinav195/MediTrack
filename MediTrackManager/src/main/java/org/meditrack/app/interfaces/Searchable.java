package org.meditrack.app.interfaces;

import org.meditrack.app.entity.Person;
import org.meditrack.app.exceptions.PersonNotFoundException;

public interface Searchable {
    public Person SearchByName(String name) throws PersonNotFoundException;
    public Person SearchByAge(int age) throws PersonNotFoundException;
    public Person SearchById(String id) throws PersonNotFoundException;

}
