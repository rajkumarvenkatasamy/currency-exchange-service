package com.example.currencyexchangeservice;

import com.example.currencyexchangeservice.model.Currency;
import com.example.currencyexchangeservice.repository.CurrencyRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CurrencyExchangeServiceApplicationTests {

    @Container
    public static OracleContainer oracleContainer =
            new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");


    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", oracleContainer::getJdbcUrl);
        registry.add("spring.datasource.password", oracleContainer::getPassword);
        registry.add("spring.datasource.username", oracleContainer::getUsername);
    }

    @Autowired
    CurrencyRepository currencyRepository;

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        String[] command = {"bash", "-c",
                "echo \"create table currency ( currency_numeric_code number primary key, currency varchar2(255) null, currency_alpha_code varchar2(255) null); " +
                        "\" | sqlplus -S " + oracleContainer.getUsername() + "/" +
                        oracleContainer.getPassword() + "@//localhost:1521/" + oracleContainer.getDatabaseName()
        };

        ExecResult execResult = oracleContainer.execInContainer(command);

        System.out.println("execResult is : " + execResult.getStdout());
        System.out.println("execResult error is : " + execResult.getStderr());
        System.out.println("execResult exit code is : " + execResult.getExitCode());

    }

    @Test
    @Order(1)
    void checkTableCreation() {
        List<Currency> currencies = currencyRepository.findAll();
        System.out.println("Currency table exists " + currencies.size());
    }

    @Test
    @Order(2)
    void assertByMountingFilesInContainer() throws IOException, InterruptedException {
        String dataFileName = "currency-dataset.sql";

        oracleContainer.copyFileToContainer(MountableFile.forClasspathResource(dataFileName),
                "/" + dataFileName);

        String[] command = {"sqlplus", "-s", oracleContainer.getUsername() +
                "/" + oracleContainer.getPassword() + "@//localhost:1521/" + oracleContainer.getDatabaseName(),
                "@/" + dataFileName
        };

        ExecResult execResult = oracleContainer.execInContainer(command);

        System.out.println("execResult is : " + execResult.getStdout());
        System.out.println("execResult error is : " + execResult.getStderr());
        System.out.println("execResult exit code is : " + execResult.getExitCode());

        // Assert the data load action

        List<Integer> currencyList = new ArrayList<>();
        currencyList.add(554);
        List<Currency> currencies = currencyRepository.findAllById(currencyList);
        System.out.println("Number of currencies found: " + currencies.size());
        System.out.println("Fetched currency is : " + currencies.get(0).getCurrency());
        //Thread.sleep(120000);
        assert currencies.size() == 1;
        Assertions.assertEquals("New Zealand Dollar", currencies.get(0).getCurrency());
    }

    @Test
    @Order(3)
    void assertFlywayDataInitialization() {
        // Assert the data load action

        List<Integer> currencyList = new ArrayList<>();
        currencyList.add(392);
        List<Currency> currencies = currencyRepository.findAllById(currencyList);
        System.out.println("Number of currencies found: " + currencies.size());
        System.out.println("Fetched currency is : " + currencies.get(0).getCurrency());
        //Thread.sleep(120000);
        assert currencies.size() == 1;
        Assertions.assertEquals("Yen", currencies.get(0).getCurrency());
    }

}
