package org.prashantkalkar.selfinitializingfake;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "target.app.base.url=http://localhost:8089")
public class SelfinitializingfakeApplicationTests {

	@LocalServerPort
	private int port;

	private static WireMockServer wireMockServer = new WireMockServer(options().port(8089));

	@BeforeClass
	public static void startTargetServer() {
		wireMockServer.start();
	}

	@AfterClass
	public static void stopTargetServer() {
		wireMockServer.stop();
	}

	@Before
	public void setupWireMockClient() {
		configureFor(8089);
	}

	@Test
	public void shouldAcceptRandomGetRequest() {
		stubFor(get(urlEqualTo("/randomURL"))
				.willReturn(aResponse().withBody("Hello World!")));

		given().port(port).get("/randomURL").then().statusCode(200).body(equalTo("Hello World!"));
	}

	@Test
	public void shouldAcceptRandomPostRequest() {
		stubFor(post(urlEqualTo("/randomURL"))
				.willReturn(aResponse().withBody("Hello World!")));

		given().port(port).post("/randomURL").then().statusCode(200).body(equalTo("Hello World!"));
	}

	@Test
	public void shouldServeFromCacheForSubsequentRequests() {
		stubFor(post(urlEqualTo("/randomURL"))
				.willReturn(aResponse().withBody("Hello World!")));

		given().port(port).post("/randomURL").then().statusCode(200).body(equalTo("Hello World!"));
		given().port(port).post("/randomURL").then().statusCode(200).body(equalTo("Hello World!"));

		verify(1, postRequestedFor(urlEqualTo("/randomURL")));
	}

	@Test
	public void shouldServeXMLContents() throws URISyntaxException, IOException {
		String request = readFile("/findWorkOrdersXMLRequest.xml");
		String response = readFile("/findWorkOrderResponse.xml");

		stubFor(post(urlEqualTo("/SPWorkOrderInquiry"))
				.withRequestBody(equalToXml(request))
				.willReturn(aResponse().withBody(response)));

		given().port(port).body(request).post("/SPWorkOrderInquiry").then().statusCode(200).body(equalTo(response));
		given().port(port).body(request).post("/SPWorkOrderInquiry").then().statusCode(200).body(equalTo(response));

		verify(1, postRequestedFor(urlEqualTo("/SPWorkOrderInquiry")));
	}

	private String readFile(String fileName) throws IOException, URISyntaxException {
		return FileUtils.readFileToString(
				new File(this.getClass().getResource(fileName).toURI()), StandardCharsets.UTF_8.name());
	}
}

