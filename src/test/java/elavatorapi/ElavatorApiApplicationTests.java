package elavatorapi;

import elavatorapi.controllers.ElevatorController;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ElavatorApiApplicationTests extends AbstractTest {


	@Override
	@Before
	public void setUp() {
		super.setUp();
	}
	String uri = "/api/v1";


	@Test
	void contextLoads() {
	}


	@Test
	 void getAllElevators() throws Exception {

		if (mvc == null) {
			setUp();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/get-elevators")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

	}



	@Test
	void callElevator() throws Exception {

		if (mvc == null) {
			setUp();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri + "/call-elevator")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

	}


	@Test
	void getElevatorInfo() throws Exception {

		if (mvc == null) {
			setUp();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/get-elevator-info/1")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

	}





}
