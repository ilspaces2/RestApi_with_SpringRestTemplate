package ru.job4j.auth.service;

import org.springframework.stereotype.Service;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public List<Person> findAll() {
        return (List<Person>) repository.findAll();
    }

    public Optional<Person> findById(final int id) {
        return repository.findById(id);
    }

    public Person create(Person person) {
        return repository.save(person);
    }

    public Person update(Person person) {
        return repository.save(person);
    }

    public void deleteById(final int id) {
        repository.deleteById(id);
    }
}
