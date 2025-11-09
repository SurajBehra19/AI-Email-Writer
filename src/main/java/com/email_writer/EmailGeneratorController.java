package com.email_writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*") // Consider restricting this in production
public class EmailGeneratorController {

    private final EmailGeneratorService emailGeneratorService;

    @Autowired
    public EmailGeneratorController(EmailGeneratorService emailGeneratorService) {
        this.emailGeneratorService = emailGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
        try {
            // Input validation
            if (emailRequest == null || emailRequest.getContent() == null || emailRequest.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email content cannot be empty");
            }

            String response = emailGeneratorService.generateEmailReply(emailRequest);

            // Check if response is valid
            if (response == null || response.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to generate email content");
            }

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Log the error in production
            System.err.println("Error generating email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating email: " + e.getMessage());
        } catch (Exception e) {
            // Handle unexpected errors
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while generating the email");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Email Writer API is running!");
    }
}