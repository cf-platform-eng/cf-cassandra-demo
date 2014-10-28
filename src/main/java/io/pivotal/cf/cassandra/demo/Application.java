package io.pivotal.cf.cassandra.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableCassandraRepositories(basePackages = {"io.pivotal.cf.cassandra.demo"})
@RestController
public class Application implements CommandLineRunner {

    @Autowired
    private CassandraSessionFactoryBean session;

    @Bean
    public CassandraOperations cassandraTemplate() throws Exception {
        return new CassandraTemplate(session.getObject());
    }

    @Autowired
    PersonRepository personRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping(value = "/people", method = RequestMethod.GET)
    public Iterable<Person> list() {
        return personRepository.findAll();
    }

    @RequestMapping(value = "/people", method = RequestMethod.POST)
    public ResponseEntity<Person> create(@RequestBody Person person) {
        String id = UUID.randomUUID().toString();
        person.setId(id);
        person = personRepository.save(person);
        return new ResponseEntity<>(person, HttpStatus.CREATED);
    }

    @Override
    public void run(String... args) throws Exception {
        String cql = "create table if not exists person (id text, name text, age int, primary key(id))";
        cassandraTemplate().execute(cql);
    }

    @RequestMapping(value = "/touch", method = RequestMethod.GET)
    public String touch() {
        return "Hello World!";
    }
}
