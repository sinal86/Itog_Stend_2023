package loginTests;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class LoginTest extends AbstractLoginTest {

    @Test
    void validAuth() {
        given()
                .spec(requestSpecification)
                .when()
                .post(getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Test
    void missingUsername() {
        given()
                .contentType("multipart/form-data")
                .multiPart("username", "")
                .multiPart("password", "e06e399547")
                .when()
                .post(getUrl())
                .then()
                .spec(responseSpecificationInvalid);
    }

    @Test
    void missingPassword() {
        given()
                .contentType("multipart/form-data")
                .multiPart("username", "Alex123123123")
                .multiPart("password", "")
                .when()
                .post(getUrl())
                .then()
                .spec(responseSpecificationInvalid);
    }
}


