package ru.job4j.auth.service;

import org.springframework.stereotype.Service;
import ru.job4j.auth.domain.Employee;
import ru.job4j.auth.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> findAll() {
        return (List<Employee>) repository.findAll();
    }

    public Optional<Employee> findById(final int id) {
        return repository.findById(id);
    }

    public Employee create(Employee employee) {
        return repository.save(employee);
    }

    public Employee update(Employee employee) {
        return repository.save(employee);
    }

    public void deleteById(final int id) {
        repository.deleteById(id);
    }

}
