package com.example.currencyexchangeservice;

import com.example.currencyexchangeservice.model.Currency;
import com.example.currencyexchangeservice.repository.CurrencyRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import org.testcontainers.utility.MountableFile;
import org.testcontainers.containers.Container.ExecResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CurrencyExchangeServiceApplicationTests {

    @Container
    public static OracleContainer oracleContainer =
            new OracleContainer("gvenzl/oracle-xe:18.4.0-slim")
                    //.withDatabaseName("xe")
                    .withDatabaseName("testcontainer_db")
                    .withUsername("testcontainer_user")
                    .withPassword("password")
                    .withExposedPorts(1521, 1521)
            //.waitingFor(Wait.forLogMessage("DATABASE IS READY TO USE!", 1))
            //.withStartupTimeout(Duration.ofMinutes(2L))
            ;


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

/*    @Test
    void assertUsingExecInContainer() throws InterruptedException, IOException {

        String[] command = {"bash", "-c",
                "echo \"insert into currency (currency,currency_alpha_code,currency_numeric_code) values ('Indian Rupee','INR',356); " +
                        "\" | sqlplus -S " + oracleContainer.getUsername() + "/" +
                        oracleContainer.getPassword() + "@//localhost:1521/" + oracleContainer.getDatabaseName()
        };

        ExecResult execResult = oracleContainer.execInContainer(command);

        System.out.println("execResult is : " + execResult.getStdout());
        System.out.println("execResult error is : " + execResult.getStderr());
        System.out.println("execResult exit code is : " + execResult.getExitCode());

        // Assert the data load action

        Optional<Currency> currency = currencyRepository.findById(356);
        String currencyUpdated = (currency.isPresent() ? currency.get().getCurrency() : "None");
        System.out.println("Fetched currency is : " + currencyUpdated);
        Assertions.assertEquals("Indian Rupee", currencyUpdated);
    }*/
/*
    @Test
    void assertUsingExecInContainerByFileCreationApproach() throws IOException, InterruptedException {
        String initDataFileName = "init-1.sql";

        String initScript = "insert into currency (currency,currency_alpha_code,currency_numeric_code) values ('Euro','EUR',978);\n" +
                "insert into currency (currency,currency_alpha_code,currency_numeric_code) values ('US Dollar','USD',840);\n" +
                "commit;";

        File sqlFile = File.createTempFile("init-1", ".sql");
        FileUtils.writeStringToFile(sqlFile, initScript, StandardCharsets.UTF_8);

        oracleContainer.copyFileToContainer(MountableFile.forHostPath(sqlFile.getAbsolutePath()),
                "/" + initDataFileName);

        String[] command = {"sqlplus", "-s", oracleContainer.getUsername() +
                "/" + oracleContainer.getPassword() + "@//localhost:1521/" + oracleContainer.getDatabaseName(),
                "@/" + initDataFileName
        };

        ExecResult execResult = oracleContainer.execInContainer(command);

        System.out.println("execResult is : " + execResult.getStdout());
        System.out.println("execResult error is : " + execResult.getStderr());
        System.out.println("execResult exit code is : " + execResult.getExitCode());

        // Assert the data load action

        List<Integer> currencyList = new ArrayList<>();
        currencyList.add(978);
        List<Currency> currencies = currencyRepository.findAllById(currencyList);
        System.out.println("Number of currencies found: " + currencies.size());
        System.out.println("Fetched currency is : " + currencies.get(0).getCurrency());
        //Thread.sleep(120000);
        assert currencies.size() == 1;
        Assertions.assertEquals("Euro", currencies.get(0).getCurrency());
    }
*/

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
