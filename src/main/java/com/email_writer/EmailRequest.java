package com.email_writer;

import lombok.Data;

@Data
public class EmailRequest {

    private String content;  // Changed from emailContent to match frontend
    private String tone;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    // Optional: Keep backward compatibility if needed
    public String getEmailContent() {
        return content;
    }

    public void setEmailContent(String emailContent) {
        this.content = emailContent;
    }
}