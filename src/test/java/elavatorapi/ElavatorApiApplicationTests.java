package elavatorapi;

import elavatorapi.controllers.ElevatorController;
import elavatorapi.models.requests.CallElevator;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
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
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/get-elevators-info")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

	}



	@Test
	void callElevatorWithCorrectInformation() throws Exception {

		if (mvc == null) {
			setUp();
		}

		CallElevator callElevator = new CallElevator();
		callElevator.setElevatorIdentifier("A");
		callElevator.setToFloor(10);
		callElevator.setFromFloor(0);

		String inputJson = super.mapToJson(callElevator);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri + "/call-elevator")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(200);


	}


	@Test
	void callElevatorWithElevatorIdentifierThatDoesNotExist() throws Exception {

		if (mvc == null) {
			setUp();
		}

		CallElevator callElevator = new CallElevator();
		callElevator.setElevatorIdentifier("Z");
		callElevator.setToFloor(10);
		callElevator.setFromFloor(0);

		String inputJson = super.mapToJson(callElevator);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri + "/call-elevator")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(400);


	}


	@Test
	void callElevatorWithInvalidFloor() throws Exception {

		if (mvc == null) {
			setUp();
		}

		CallElevator callElevator = new CallElevator();
		callElevator.setElevatorIdentifier("Z");
		callElevator.setToFloor(100);
		callElevator.setFromFloor(0);

		String inputJson = super.mapToJson(callElevator);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri + "/call-elevator")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(400);


	}


	@Test
	void callElevatorFromTheSameFloor() throws Exception {

		if (mvc == null) {
			setUp();
		}

		CallElevator callElevator = new CallElevator();
		callElevator.setElevatorIdentifier("A");
		callElevator.setToFloor(2);
		callElevator.setFromFloor(2);

		String inputJson = super.mapToJson(callElevator);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri + "/call-elevator")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(400);


	}

}
