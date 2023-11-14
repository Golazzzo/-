import io.restassured.RestAssured;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import io.restassured.response.Response;

public class StoreAPITest {

    @Test
    public void testPlaceOrder() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"id\": 9223372036854762000,\n" +
                        "  \"petId\": 0,\n" +
                        "  \"quantity\": 0,\n" +
                        "  \"shipDate\": \"2023-11-13T23:17:44.317+0000\",\n" +
                        "  \"status\": \"placed\",\n" +
                        "  \"complete\": true\n" +
                        "}")
                .when()
                .post("/store/order")
                .then()
                .statusCode(200)
                .body("id", equalTo(9223372036854762000L))
                .body("petId", equalTo(0))
                .body("quantity", equalTo(0))
                .body("shipDate", equalTo("2023-11-13T23:17:44.317+0000"))
                .body("status", equalTo("placed"))
                .body("complete", equalTo(true));
    }

    @Test
    public void testPlaceOrder400() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"id\": 9223372036854762001,\n" +
                        "  \"petId\": 0,\n" +
                        "  \"quantity\": 0,\n" +
                        "  \"shipDate\": \"2023-11-13T23:17:44.317+0000\",\n" +
                        "  \"status\": \"placed\",\n" +
                        "  \"complete\": false\n" + // Изменим значение complete на false
                        "}")
                .when()
                .post("/store/order")
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid Order")); // Проверка на ожидаемое сообщение об ошибке
    }
    @Test
    public void testGetOrderById() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Создаем заказ для последующего запроса по его ID
        Response responsePlaceOrder = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"id\": 9223372036854762000,\n" +
                        "  \"petId\": 0,\n" +
                        "  \"quantity\": 0,\n" +
                        "  \"shipDate\": \"2023-11-13T23:38:38.456Z\",\n" +
                        "  \"status\": \"placed\",\n" +
                        "  \"complete\": true\n" +
                        "}")
                .when()
                .post("/store/order")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Получаем ID созданного заказа из ответа
        long orderId = responsePlaceOrder.jsonPath().getLong("id");

        // Запрос по ID созданного заказа
        given()
                .pathParam("orderId", orderId)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("petId", equalTo(0))
                .body("quantity", equalTo(0))
                .body("shipDate", equalTo("2023-11-13T23:38:38.456Z"))
                .body("status", equalTo("placed"))
                .body("complete", equalTo(true));
    }
    @Test
    public void testGetOrderNotFound() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Предполагаем, что orderId 999 не существует
        int nonExistentOrderId = 999;

        // Запрос с несуществующим orderId, ожидается код ошибки 404
        given()
                .pathParam("orderId", nonExistentOrderId)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404)
                .body("message", equalTo("Order not found"));
    }
    @Test
    public void testCreateAndDeleteOrder() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Создаем заказ и получаем его ID
        Long orderId = createOrder();

        // Проверяем, что заказ был успешно создан (пример)
        System.out.println("Order created with ID: " + orderId);

        // Удаляем заказ
        deleteOrder(orderId);
    }

    // Метод для создания заказа
    private Long createOrder() {
        return given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"id\": 9223372036854762000,\n" +
                        "  \"petId\": 0,\n" +
                        "  \"quantity\": 0,\n" +
                        "  \"shipDate\": \"2023-11-13T23:17:44.317+0000\",\n" +
                        "  \"status\": \"placed\",\n" +
                        "  \"complete\": true\n" +
                        "}")
                .when()
                .post("/store/order")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    // Метод для удаления заказа по его ID
    private void deleteOrder(Long orderId) {
        given()
                .pathParam("orderId", orderId)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);
        System.out.println("Order with ID " + orderId + " deleted successfully.");

        // Пытаемся удалить заказ еще раз с ожидаемым кодом 404
        given()
                .pathParam("orderId", orderId)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(404)
                .body("message", equalTo("Order Not Found"));
        System.out.println("Attempted to delete non-existing order with ID " + orderId + ". Expected 404.");
    }

}