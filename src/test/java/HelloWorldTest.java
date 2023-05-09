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
    @Test
    public  void testEx8Tokens() throws InterruptedException {
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
        System.out.println("Getting 'token' and 'seconds' for create a job...");
        JsonPath responseForGetTokenAndSeconds  = RestAssured
                .get(url)
                .jsonPath();
        String tokenForJob = responseForGetTokenAndSeconds.get("token");
        int secondsForWait = responseForGetTokenAndSeconds.get("seconds");
        int millisecondsForWait = secondsForWait * 1000;
        responseForGetTokenAndSeconds.prettyPrint();
        System.out.println();

        System.out.println("Checking errors with not correct token...");
        JsonPath responseWithNotCorrectToken  = RestAssured
                .given()
                .queryParam("token", "notCorrectToken")
                .get(url)
                .jsonPath();
        String errorWithNotCorrectToken = responseWithNotCorrectToken.get("error");
        responseWithNotCorrectToken.prettyPrint();
        if(errorWithNotCorrectToken.equals("No job linked to this token")) {
            System.out.println("Error message is correct. It is: " + errorWithNotCorrectToken);
        }else {
            System.out.println("Error message is NOT correct. It is: " + errorWithNotCorrectToken);
        }
        System.out.println();

        System.out.println("Checking job status without waiting ...");
        JsonPath responseWithoutWaiting  = RestAssured
                .given()
                .queryParam("token", tokenForJob)
                .get(url)
                .jsonPath();
        String statusWithoutWait = responseWithoutWaiting.get("status");
        responseWithoutWaiting.prettyPrint();
        if (statusWithoutWait.equals("Job is NOT ready")) {
            System.out.println("Jobs status without waiting is: '" + statusWithoutWait + "'. It is correct!!!");
        }else {
            System.out.println("Jobs status without waiting is: '" + statusWithoutWait + "'. It is NOT correct!!!");
        }
        System.out.println();

        System.out.println("Waiting " + secondsForWait + " seconds to create a job ...");
        Thread.sleep(millisecondsForWait);

        System.out.println("Checking job status after waiting ...");
        JsonPath responseWithWaiting  = RestAssured
                .given()
                .queryParam("token", tokenForJob)
                .get(url)
                .jsonPath();
        responseWithWaiting.prettyPrint();
        String statusAfterWait = responseWithWaiting.get("status");
        String resultAfterWait = responseWithWaiting.get("result");
        if (statusAfterWait.equals("Job is ready")) {
            System.out.println("Jobs status after waiting is: '" + statusAfterWait + "'. It is correct!!!");
        }else {
            System.out.println("Jobs status after waiting is: '" + statusAfterWait + "'. It is NOT correct!!!");
        }
        if (resultAfterWait != null) {
            System.out.println("Result is not empty. It is: " + resultAfterWait);
        }else {
            System.out.println("There is no result");
        }
    }
}
