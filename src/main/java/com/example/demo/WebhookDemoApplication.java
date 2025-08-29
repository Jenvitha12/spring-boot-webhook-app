package main.java.com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@SpringBootApplication
public class WebhookDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebhookDemoApplication.class, args);
    }
}

@Component
class StartupService {

    // This method runs automatically when the app starts
    @EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void atStartup() {
        System.out.println("Startup logic running..."); // Verification log

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 1. Send POST to generate webhook
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            JSONObject body = new JSONObject();
            body.put("name", "John Doe"); // Use your name here
            body.put("regNo", "REG12347"); // Use your regNo
            body.put("email", "john@example.com"); // Use your email

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JSONObject respJson = new JSONObject(response.getBody());

            String webhookUrl = respJson.getString("webhookUrl");
            String accessToken = respJson.getString("accessToken");
            String regNo = body.getString("regNo");

            System.out.println("Got webhookUrl: " + webhookUrl); // Log for verification
            System.out.println("Got accessToken: " + accessToken);

            // 2. Download and solve the SQL question (manual for now)
            int lastTwoDigits = Integer.parseInt(regNo.replaceAll("\\D", "")) % 100;
            String questionLink = (lastTwoDigits % 2 == 0)
                    ? "https://drive.google.com/file/d/143MR5eLFylNEuHzxWJ5RHnEW-jtJuM9X/view?usp=sharing"
                    : "https://drive.google.com/file/d/1IeSl6I6KoSqAFjRihT9tEDICtoz-Gj/view?usp=sharing";
            System.out.println("Your SQL question link: " + questionLink);

            // TODO: Download and read the question, solve it; enter your final SQL query
            // below.
            String finalQuery = "SELECT * FROM your_table"; // Replace this with your answer!

            // 3. Submit final SQL query
            String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
            JSONObject submitBody = new JSONObject();
            submitBody.put("finalQuery", finalQuery);

            HttpHeaders submitHeaders = new HttpHeaders();
            submitHeaders.setContentType(MediaType.APPLICATION_JSON);
            submitHeaders.setBearerAuth(accessToken); // Auth with JWT

            HttpEntity<String> submitEntity = new HttpEntity<>(submitBody.toString(), submitHeaders);

            ResponseEntity<String> submitResponse = restTemplate.postForEntity(submitUrl, submitEntity, String.class);
            System.out.println("Submission response: " + submitResponse.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
