package postsTests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MyPostsTest extends AbstractPostsTest {

    @Test
    void validGetPosts() {
        given()
                .spec(requestSpecification)
                .when()
                .get(getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Test
    void unauthorizedTryToGetPosts() {
        given()
                .when()
                .get(getUrl())
                .then()
                .spec(responseSpecificationInvalid);
    }

    @Test
    void invalidTokenTryToGetPosts() {
        Response response = given()
                .header("X-Auth-Token", "111")
                .when()
                .get(getUrl())
                .then()
                .spec(responseSpecificationInvalid)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.get("message"), equalTo("No API token provided or is not valid"));
    }

    @Test
    void doesNotHaveDataFromInvalidPage() {
        Response response = given()
                .spec(requestSpecification)
                .when()
                .get(getUrl() + "?page=99999999")
                .then()
                .spec(responseSpecification)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> list = jsonPath.getList("data");

        assertThat(list, hasSize(0));
    }

    @Test
    void responseHasCorrectPaging() {
        int availablePages;
        int posts;
        int randomPageValue;

        Response response = given()
                .spec(requestSpecification)
                .when()
                .get(getUrl())
                .then()
                .spec(responseSpecification)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        Map<String, Integer> map = jsonPath.getMap("meta");
        posts = map.get("count");
        availablePages = (posts/4)+1;
        randomPageValue = (int)(Math.random() * (availablePages+1));

        Response mainResponse = given()
                .spec(requestSpecification)
                .when()
                .get(getUrl() + "?page=" + randomPageValue)
                .then()
                .spec(responseSpecification)
                .extract().response();

        JsonPath mainJsonPath = mainResponse.jsonPath();
        Map<String, Integer> mainMap = mainJsonPath.getMap("meta");

        Integer prevPage = mainMap.get("prevPage");
        Integer nextPage = mainMap.get("nextPage");

        if(randomPageValue==0) {
            assertThat(prevPage, is(0));
            assertThat(nextPage, is(1));
        } else if(randomPageValue>0 && randomPageValue<availablePages) {
            assertThat(prevPage, is(randomPageValue-1));
            assertThat(nextPage, is(randomPageValue+1));
        } else if(randomPageValue==availablePages) {
            assertThat(prevPage, is(randomPageValue-1));
            assertThat(nextPage, nullValue());
        }
    }
}

