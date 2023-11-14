import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetStoreAPITest {

    @Test
    public void testGetPetsByStatus() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Запрос для статуса 'available'
        Response responseAvailable = given()
                .param("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Проверка, что хотя бы один питомец существует
        responseAvailable.then().body("size()", greaterThan(0));

        // Запрос для статуса 'pending'
        Response responsePending = given()
                .param("status", "pending")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Проверка, что хотя бы один питомец существует
        responsePending.then().body("size()", greaterThan(0));

        // Запрос для статуса 'sold'
        Response responseSold = given()
                .param("status", "sold")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Проверка, что хотя бы один питомец существует
        responseSold.then().body("size()", greaterThan(0));


    }
    @Test
    public void testGetPetsByStatus400() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        // Запрос с недопустимым статусом, ожидается код ошибки 400
        given()
                .param("status", "invalid_status_value")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(400);
    }
    @Test
    public void testAddNewPetToStore() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Запрос для добавления нового питомца
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"id\": 10,\n" +
                        "  \"category\": {\n" +
                        "    \"id\": 0,\n" +
                        "    \"name\": \"string\"\n" +
                        "  },\n" +
                        "  \"name\": \"doggie\",\n" +
                        "  \"photoUrls\": [\n" +
                        "    \"string\"\n" +
                        "  ],\n" +
                        "  \"tags\": [\n" +
                        "    {\n" +
                        "      \"id\": 0,\n" +
                        "      \"name\": \"string\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"status\": \"available\"\n" +
                        "}")
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("id", equalTo(10))
                .body("category.id", equalTo(0))
                .body("category.name", equalTo("string"))
                .body("name", equalTo("doggie"))
                .body("photoUrls[0]", equalTo("string"))
                .body("tags[0].id", equalTo(0))
                .body("tags[0].name", equalTo("string"))
                .body("status", equalTo("available"));
    }
    @Test
    public void testInvalidInputForAddNewPet() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Запрос с неверным форматом данных, ожидается код ошибки 405
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"invalidField\": \"invalidValue\"\n" +
                        "}")
                .when()
                .post("/pet")
                .then()
                .statusCode(405);
    }
    @Test
    public void testDeletePetById() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Создаем нового питомца
        int petId = 999; // Замените на реальный ID, если требуется

        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"id\": " + petId + ",\n" +
                        "  \"category\": {\n" +
                        "    \"id\": 0,\n" +
                        "    \"name\": \"string\"\n" +
                        "  },\n" +
                        "  \"name\": \"deletePet\",\n" +
                        "  \"photoUrls\": [\n" +
                        "    \"string\"\n" +
                        "  ],\n" +
                        "  \"tags\": [\n" +
                        "    {\n" +
                        "      \"id\": 0,\n" +
                        "      \"name\": \"string\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"status\": \"available\"\n" +
                        "}")
                .when()
                .post("/pet");

        // Удаляем питомца
        given()
                .pathParam("petId", petId)
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(200);

        // Проверяем, что питомец успешно удален
        given()
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404);
    }

}
