package loginTests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AbstractLoginTest {

    private static Properties prop = new Properties();
    private static InputStream configFile;
    private static String baseUrl;
    protected static ResponseSpecification responseSpecification;
    protected static ResponseSpecification responseSpecificationInvalid;
    protected static RequestSpecification requestSpecification;

    @BeforeAll
    static void initTest() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        configFile = new FileInputStream("src/main/resources/my.properties");
        prop.load(configFile);

        baseUrl= prop.getProperty("base_url");

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(2000L))
                .build();

        responseSpecificationInvalid = new ResponseSpecBuilder()
                .expectStatusCode(401)
                .expectStatusLine("HTTP/1.1 401 Unauthorized")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(2000L))
                .build();

        requestSpecification = new RequestSpecBuilder()
                .setContentType("multipart/form-data")
                .addMultiPart("username", "Alex123123123")
                .addMultiPart("password", "e06e399547")
                .build();
    }

    public static String getUrl() {
        return baseUrl + "gateway/login";
    }
}

