# 📧 AI Email Writer

**AI Email Writer** is a smart web application that automatically generates **professional and personalized emails** using Artificial Intelligence.  
Powered by **Google Gemini API**, it converts short text prompts into complete, grammatically correct, and well-structured emails within seconds.

---

## 🚀 Features

- 🧠 AI-powered email generation using **Gemini API**
- ✍️ Supports multiple tones — Formal, Casual, and Friendly  
- ⚙️ Built with **Spring Boot** (backend) and **HTML, CSS, JavaScript** (frontend)  
- 🔗 REST API integration between frontend and backend  
- 💾 Users can copy, edit, or reuse generated emails  
- 🌐 Simple, clean, and responsive interface  

---

## 🛠️ Tech Stack

**Backend:** Spring Boot (Java)  
**Frontend:** HTML, CSS, JavaScript  
**AI Integration:** Google Gemini API  
**Database (Optional):** H2 / MySQL / Oracle  

---

## ⚙️ Setup Instructions

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/<SurajBehra19>/Email_Writer.git
cd Email_Writer
2️⃣ Run the Backend (Spring Boot)
bash
Copy code
mvn spring-boot:run
Server runs on: http://localhost:9090

3️⃣ Access Frontend
Open the index.html file in your browser,
or place it inside the resources/static folder of your Spring Boot app.

🔑 Gemini API Configuration
Get your API key from Google AI Studio.

Add it in your Spring Boot configuration file:

application.properties

properties
Copy code
gemini.api.key=YOUR_GEMINI_API_KEY
Use it in your service class:


json
Copy code
{
  "prompt": "Write an email to HR requesting leave for 2 days"
}
Response:

json
Copy code
{
  "email": "Dear HR, I hope you're doing well. I’d like to request leave for 2 days..."
}
📂 Project Structure
css
Copy code
Email_Writer/
│
├── src/
│   ├── main/java/com/emailwriter/
│   │   ├── controller/EmailController.java
│   │   ├── service/EmailService.java
│   │   └── model/EmailRequest.java
│   ├── main/resources/
│   │   ├── static/           # HTML, CSS, JS files
│   │   └── application.properties
│
├── pom.xml
└── README.md
🤖 How It Works
User enters a prompt (like “Write a follow-up email to a recruiter”).

Spring Boot backend sends the request to Gemini API.

Gemini generates a complete and professional email.

The frontend displays it instantly with edit and copy options.

🧑‍💻 Author
Suraj Behra
