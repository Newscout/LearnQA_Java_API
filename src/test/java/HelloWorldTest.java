import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {
        Response response =RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }
    @Test
    public void testEx5ParsingJson() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        String answer = response.get("messages[1].message");
        System.out.println(answer);
    }
    @Test
    public void testEx6Redirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        String locationHeader = response.getHeader("location");
        System.out.println(locationHeader);
    }
    @Test
    public void testEx7LongRedirect() {

        int statusCode = 100;
        int countRedirects = 0;
        String url = "https://playground.learnqa.ru/api/long_redirect";
        while(statusCode != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();
            statusCode = response.getStatusCode();
            if(statusCode != 200) {
                url = response.getHeader("location");
                countRedirects++;
                System.out.println(statusCode + "  ---  " + url);
            }
        }
        System.out.println("The number of redirects is " + countRedirects + ".");
    }

}
