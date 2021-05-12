package com.raulmello.crud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raulmello.crud.model.Employee;
import com.raulmello.crud.repository.EmployeeRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EmployeeControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper mapper;

    private MockMvc mockMvc;

    private Employee createTestEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Teste");
        employee.setLastName("JUnit");
        employee.setInscriptionId("0000000001");
        employee.setPosition("Testes");
        employee.setBranch("LATAM");
        employee.setManager("Superior");
        employee.setIncome(1000);

        employeeRepository.save(employee);

        return employee;
    }

    private void deleteTestEmployee(Employee employee) {
        employeeRepository.delete(employee);
    }

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    @Test
    @DisplayName("Should find all employee records in database")
    void should_findAll() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/employees").characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult.getResponse());
    }

    @Test
    @DisplayName("Should get a single employee record according to its id number")
    void should_findById() throws Exception {
        Employee employee = createTestEmployee();

        MvcResult mvcResult = this.mockMvc
                .perform(get("/employee/{id}", employee.getId()).characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Employee foundEmployee = mapper.readValue(mvcResult.getResponse().getContentAsString(), Employee.class);

        assertEquals(employee.getId(), foundEmployee.getId());

        deleteTestEmployee(employee);

    }

    @Test
    @DisplayName("Should create a new employee record in database")
    void should_create() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(post("/employee")
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\n" +
                                "    \"firstName\": \"Teste\",\n" +
                                "    \"lastName\": \"JUnit\",\n" +
                                "    \"inscriptionId\": \"000000000\",\n" +
                                "    \"position\": \"Testes\",\n" +
                                "    \"branch\": \"Latam\",\n" +
                                "    \"manager\": \"Superior\",\n" +
                                "    \"income\": 1000\n" +
                                "}")
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Employee employee = mapper.readValue(mvcResult.getResponse().getContentAsString(), Employee.class);

        assertEquals("Teste", employee.getFirstName());
        assertEquals("JUnit", employee.getLastName());
        assertEquals("000000000", employee.getInscriptionId());
        assertEquals("Testes", employee.getPosition());
        assertEquals("Latam", employee.getBranch());
        assertEquals("Superior", employee.getManager());
        assertEquals(1000, employee.getIncome());

        deleteTestEmployee(employee);
    }

    @Test
    @DisplayName("Should update an employee field according to its id number")
    void should_update() throws Exception {
        Employee employee = createTestEmployee();

        MvcResult mvcResult = this.mockMvc
                .perform(put("/employee/{id}", employee.getId())
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\n" +
                                "    \"firstName\": \"Teste1\",\n" +
                                "    \"lastName\": \"JUnit1\",\n" +
                                "    \"inscriptionId\": \"000000001\",\n" +
                                "    \"position\": \"Testes1\",\n" +
                                "    \"branch\": \"Latam1\",\n" +
                                "    \"manager\": \"Superior1\",\n" +
                                "    \"income\": 1001\n" +
                                "}")
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Employee foundEmployee = mapper.readValue(mvcResult.getResponse().getContentAsString(), Employee.class);

        assertEquals(employee.getId(), foundEmployee.getId());
        assertEquals("Teste1", foundEmployee.getFirstName());
        assertEquals("JUnit1", foundEmployee.getLastName());
        assertEquals("000000001", foundEmployee.getInscriptionId());
        assertEquals("Testes1", foundEmployee.getPosition());
        assertEquals("Latam1", foundEmployee.getBranch());
        assertEquals("Superior1", foundEmployee.getManager());
        assertEquals(1001, foundEmployee.getIncome());

        deleteTestEmployee(employee);
    }

    @Test
    @DisplayName("Should partially update an employee record according to ist id number")
    void should_partially_update() throws Exception {
        Employee employee = createTestEmployee();

        MvcResult mvcResult = this.mockMvc
                .perform(patch("/employee/{id}", employee.getId())
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"lastName\": \"TesteAtualizado\", \"manager\": \"Chefe\" }"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Employee updatedEmployee = mapper.readValue(mvcResult.getResponse().getContentAsString(), Employee.class);

        assertEquals(employee.getId(), updatedEmployee.getId());
        assertEquals("TesteAtualizado", updatedEmployee.getLastName());
        assertEquals("Chefe", updatedEmployee.getManager());

        deleteTestEmployee(employee);
    }

    @Test
    @DisplayName("Should delete an employee record according to its id number")
    void should_delete() throws Exception {
        Employee employee = createTestEmployee();

        MvcResult mvcResult = this.mockMvc
                .perform(delete("/employee/{id}", employee.getId())
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn();

        Employee foundEmployee = mapper.readValue(mvcResult.getResponse().getContentAsString(), Employee.class);

        assertEquals(foundEmployee.getId(), employee.getId());

    }

}