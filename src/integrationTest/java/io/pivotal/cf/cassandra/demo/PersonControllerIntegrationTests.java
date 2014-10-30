package io.pivotal.cf.cassandra.demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PersonControllerIntegrationTests {

    @Autowired
    CassandraOperations cassandraTemplate;

    @Before
    public void setUp() {
        String cql = "create table if not exists person (id text, name text, age int, primary key(id))";
        cassandraTemplate.execute(cql);

        cql = "insert into person (id,name,age) values ('1234567890','Matt',35)";
        cassandraTemplate.execute(cql);
    }

    @Test
    public void findAll() {
        given()
                .when()
                .get("/people")

                .then().log().all()
                .statusCode(200)

                .body("id", hasItems("1234567890"))
                .body("name", hasItems("Matt"))
                .body("age", hasItems(35));
    }

    @After
    public void tearDown() {
        String cql = "truncate person";
        cassandraTemplate.execute(cql);
    }

}
