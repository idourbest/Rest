import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;

public class NewsTest {


    public static final String BASE_URI = "https://newsapi.org";
    public static final String endPointNews = "/v2/top-headlines?sources=bbc-news&apiKey=";
    public static final String API_KEY = "0eed3df46e9044359ea7b62966faa08b";

    public static RequestSpecification specification;

    @BeforeAll
    static void setUp() {
        specification = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .log(LogDetail.ALL)
                .setAccept(ContentType.JSON)
                .build();
    }


    @DisplayName("Check for correct connection")
    @Test
    void getNewsTest() {
        given().spec(specification)
                .when().get(endPointNews + API_KEY)
                .then().statusCode(200);
    }

    @DisplayName("Check for main fields containing properties")
    @Test
    void getMainFieldsTest() {
        given().spec(specification)
                .when().get(endPointNews + API_KEY)
                .then().body("articles.author", Matchers.notNullValue())
                .and().body("articles.title", Matchers.notNullValue())
                .and().body("articles.description", Matchers.notNullValue())
                .and().body("articles.url", Matchers.notNullValue());
    }

    @DisplayName("Check for author field contains only BBC News")
    @ParameterizedTest
    @ValueSource(strings = {"New York Times", "The Wall Street Journal", "USA Today", "CBS", "NBC", "ABC"})
    void getFirstElementParameters(String infoAgency) {
        given().spec(specification)
                .when().get(endPointNews + API_KEY)
                .then().body("articles.author", Matchers.not(infoAgency))
                .and().body("articles.author", Matchers.anything("BBC News"));
    }
}
