package ru.job4j.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.domain.Employee;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.service.EmployeeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private RestTemplate rest;

    private final EmployeeService employeeService;

    private static final String API_PERSON = "http://localhost:8080/person/";

    private static final String API_PERSON_ID = "http://localhost:8080/person/{id}";

    public EmployeeController(RestTemplate rest, EmployeeService employeeService) {
        this.rest = rest;
        this.employeeService = employeeService;
    }

    /**
     * Вывести всех работников с аккаунтами
     */
    @GetMapping("/")
    public List<Employee> findAllEmployee() {
        return employeeService.findAll();
    }

    /**
     * Вывести работника с аккаунтами по id работника
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> findByIdEmployee(@PathVariable int id) {
        var employee = employeeService.findById(id);
        return new ResponseEntity<>(
                employee.orElse(null),
                employee.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    /**
     * Создаем работника. Можно указать новые аккаунты или не заполнять поле с аккаунтами.
     */
    @PostMapping("/")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return new ResponseEntity<>(
                employeeService.create(employee),
                HttpStatus.CREATED);
    }

    /**
     * Удалить сотрудника
     */
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int employeeId) {
        employeeService.deleteById(employeeId);
        return ResponseEntity.ok().build();
    }

    /**
     * Добавить новые аккаунты к работнику
     */
    @PostMapping("/addNewAccount/{employeeId}")
    public ResponseEntity<Employee> addNewAccount(@PathVariable int employeeId, @RequestBody List<Person> persons) {
        Optional<Employee> employeeOptional = employeeService.findById(employeeId);
        if (employeeOptional.isEmpty() || persons.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = employeeOptional.get();
        persons.forEach(person -> employee.addPerson(rest.postForObject(API_PERSON, person, Person.class)));
        return new ResponseEntity<>(
                employeeService.update(employee),
                HttpStatus.OK);
    }

    /**
     * Добавить аккаунты, из имеющихся в базе данных, к работнику
     */
    @PostMapping("/addAccountFromAccounts/{employeeId}")
    public ResponseEntity<Employee> addAccountFromAccounts(@PathVariable int employeeId, @RequestBody List<Integer> idAccounts) {
        Optional<Employee> employeeOptional = employeeService.findById(employeeId);
        if (employeeOptional.isEmpty() || idAccounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = employeeOptional.get();
        for (var id : idAccounts) {
            var person = rest.getForObject(API_PERSON_ID, Person.class, id);
            if (person == null) {
                continue;
            }
            employee.addPerson(person);
        }
        return new ResponseEntity<>(
                employeeService.update(employee),
                HttpStatus.OK);
    }

    /**
     * Обновление аккаунтов.
     */
    @PutMapping("/updateAccount")
    public ResponseEntity<Void> updateAccount(@RequestBody List<Person> persons) {
        persons.forEach(person -> rest.put(API_PERSON, person));
        return ResponseEntity.ok().build();
    }

    /**
     * Удалить аккаунт у работника по его id и по id аккаунта
     */
    @DeleteMapping("/deleteAccount/{employeeId}/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable int accountId, @PathVariable int employeeId) {
        Optional<Employee> employeeOptional = employeeService.findById(employeeId);
        if (employeeOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = employeeOptional.get();
        if (!employee.getPersons().removeIf(el -> el.getId() == accountId)) {
            return ResponseEntity.notFound().build();
        }
        employeeService.update(employee);
        rest.delete(API_PERSON_ID, accountId);
        return ResponseEntity.ok().build();
    }
}
