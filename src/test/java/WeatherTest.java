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

public class WeatherTest {

    public static final String BASE_URI = "http://api.weatherapi.com/";
    public static final String endPointWeather = "v1/current.json?key=";
    public static final String API_KEY = "1d3385a9dc6545e2805122902210703";
    public static final String parameter = "&q=London&aqi=no";

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
    void getWeatherTest() {
        given().spec(specification)
                .when().get(endPointWeather + API_KEY + parameter)
                .then().statusCode(200);
    }

    @DisplayName("Check for main fields containing right properties")
    @Test
    void getMainFieldsTest() {
        given().spec(specification)
                .when().get(endPointWeather + API_KEY + parameter)
                .then().body("location.name", Matchers.notNullValue())
                .and().body("current.last_updated", Matchers.notNullValue());
    }

    @DisplayName("Check for name field doesn't containing other cities and containing only London")
    @ParameterizedTest
    @ValueSource(strings = {"Moscow", "York", "Berlin", "Paris"})
    void getFirstElementParameters(String city) {
        given().spec(specification)
                .when().get(endPointWeather + API_KEY + parameter)
                .then().body("location.name", Matchers.not(city))
                .and().body("location.name", Matchers.equalTo("London"));
    }
}
