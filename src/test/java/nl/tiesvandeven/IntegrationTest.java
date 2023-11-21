package nl.tiesvandeven;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.AnythingPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import nl.tiesvandeven.models.InitialResult;
import nl.tiesvandeven.models.InitialResults;
import org.apache.commons.lang3.stream.IntStreams;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

@WireMockTest
public class IntegrationTest {

	@Test
	void test_something_with_wiremock(WireMockRuntimeInfo wmRuntimeInfo) throws IOException, InterruptedException {
		ObjectMapper mapper = new ObjectMapper();
		var results = new InitialResults(IntStream.range(0, 100).mapToObj(i -> new InitialResult("test")).toList());

		stubFor(get("/testurl").willReturn(ok().withBody(mapper.writeValueAsString(results))));

		stubFor(get(urlPathEqualTo("/testurl2")).withQueryParam("name", containing("test")).willReturn(ok().withFixedDelay(1000).withBody("result")));

		System.out.println(wmRuntimeInfo.getHttpBaseUrl());
		Client client = new Client(wmRuntimeInfo.getHttpBaseUrl());
		Logic logic = new Logic(client);
		logic.doLogic();

	}

}
