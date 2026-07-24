# 🤖 AI Interview Platform

An AI-powered interview preparation platform built using **Java, Spring Boot, React, PostgreSQL, Redis, Docker, Kubernetes, and Generative AI**.

The platform helps candidates prepare for technical interviews by generating personalized questions from their resume, conducting mock interviews, evaluating answers using AI, and providing structured feedback.

---

## 📌 Project Overview

The AI Interview Platform is designed to provide a personalized interview preparation experience.

Users can:

* Register and securely log in
* Upload their resume
* Extract skills, projects, and experience from the resume
* Generate categorized interview questions
* Practice 30-second and detailed 2-minute answers
* Attend AI-powered mock interviews
* Submit answers and receive AI-generated feedback
* Track interview scores and progress
* Improve generated answers by providing feedback

The platform generates questions based on the candidate’s actual experience, skills, and projects instead of providing only generic interview questions.

---

## ✨ Key Features

### 🔐 Authentication and Authorization

* User registration and login
* JWT-based authentication
* Password encryption using BCrypt
* Role-based access control
* Secure protected APIs
* Current-user profile endpoint

### 📄 Resume Management

* Upload resumes in PDF or DOCX format
* Store resume metadata and storage location
* Parse resume content using AI
* Extract:

  * Candidate name
  * Professional title
  * Total experience
  * Technical skills
  * Projects
  * Responsibilities
  * Achievements
  * Technologies used

### 🧠 AI Question Bank

* Generate interview questions based on resume keywords
* Categorize questions into sections such as:

  * Core Java
  * Spring Boot
  * Microservices
  * React
  * SQL
  * PostgreSQL
  * Redis
  * Kafka
  * Docker
  * Kubernetes
  * AWS
  * System Design
* Generate project-specific interview questions
* Store generated questions for future revision
* Generate:

  * 30-second interview-ready answer
  * 2-minute detailed explanation
  * Technical examples
  * Project-based examples
  * Common follow-up questions
  * Common mistakes
  * What not to say in an interview

### 🎙️ Mock Interview Module

* Start a new interview session
* Generate questions dynamically
* Submit answers question by question
* Track answered and pending questions
* Complete the interview automatically
* Generate overall interview results
* Store interview history

### 📊 AI Answer Evaluation

* Evaluate submitted answers using Generative AI
* Generate a score for every answer
* Provide strengths and improvement areas
* Generate a better interview-ready answer
* Provide technical and communication feedback
* Store AI feedback for later revision

### 💬 Feedback-Based Answer Improvement

* Users can provide feedback on generated answers
* AI regenerates answers using the additional project context
* Answers become more personalized over time
* Updated answers are stored in the question bank

### ⚡ Performance and Scalability

* Redis caching
* PostgreSQL database
* Docker containerization
* Kubernetes-ready deployment
* Stateless JWT authentication
* Layered and modular backend architecture

---

## 🏗️ System Architecture

```text
                         ┌──────────────────────┐
                         │      React UI        │
                         │   Vite + Axios       │
                         └──────────┬───────────┘
                                    │
                                    │ REST API
                                    ▼
                    ┌─────────────────────────────┐
                    │      Spring Boot API        │
                    │                             │
                    │  Auth | Resume | Interview  │
                    │  Question Bank | AI Module  │
                    └───────┬─────────┬───────────┘
                            │         │
                 ┌──────────▼───┐ ┌──▼──────────────┐
                 │ PostgreSQL   │ │ Generative AI   │
                 │ Main Storage │ │ Gemini / OpenAI │
                 └──────────┬───┘ └─────────────────┘
                            │
                     ┌──────▼──────┐
                     │    Redis    │
                     │ Cache/State │
                     └─────────────┘
```

---

## 🛠️ Technology Stack

### Backend

* Java 17+
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* Spring Validation
* Spring WebFlux WebClient
* JWT Authentication
* Gradle
* Lombok
* Swagger / OpenAPI

### Frontend

* React
* Vite
* JavaScript
* Axios
* React Router
* HTML5
* CSS3

### Database and Caching

* PostgreSQL
* Redis

### AI Integration

* Google Gemini API
* OpenAI-compatible architecture
* Structured prompt generation
* AI-based resume parsing
* AI-based answer evaluation

### DevOps and Deployment

* Docker
* Docker Compose
* Kubernetes
* AWS-ready deployment
* Git and GitHub

### Testing

* JUnit 5
* Mockito
* Spring Boot Test
* MockMvc

---

## 📁 Project Structure

### Backend Structure

```text
src/main/java/com/aiinterviewcoach
│
├── auth
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── service
│   └── security
│
├── user
│   ├── entity
│   ├── repository
│   └── service
│
├── resume
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── mapper
│   └── service
│
├── candidate
│   ├── entity
│   ├── repository
│   ├── mapper
│   └── service
│
├── questionbank
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── mapper
│   └── service
│
├── interview
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── mapper
│   └── service
│
├── ai
│   ├── client
│   ├── dto
│   ├── prompt
│   ├── parser
│   └── service
│
├── config
│   ├── SecurityConfig
│   ├── CorsConfig
│   ├── RedisConfig
│   ├── WebClientConfig
│   └── SwaggerConfig
│
├── exception
│   ├── GlobalExceptionHandler
│   ├── ResourceNotFoundException
│   ├── UnauthorizedException
│   ├── BadRequestException
│   ├── AiProcessingException
│   └── FileStorageException
│
└── AiInterviewCoachApplication
```

### Frontend Structure

```text
src
│
├── api
│   ├── axiosInstance.js
│   ├── authApi.js
│   ├── resumeApi.js
│   ├── questionBankApi.js
│   └── interviewApi.js
│
├── components
│   ├── common
│   ├── auth
│   ├── resume
│   ├── questionBank
│   └── interview
│
├── pages
│   ├── LoginPage.jsx
│   ├── RegisterPage.jsx
│   ├── DashboardPage.jsx
│   ├── ResumeUploadPage.jsx
│   ├── QuestionBankPage.jsx
│   ├── InterviewPage.jsx
│   └── InterviewResultPage.jsx
│
├── routes
│   └── ProtectedRoute.jsx
│
├── utils
│   └── tokenStorage.js
│
├── App.jsx
└── main.jsx
```

---

## 🔄 Application Workflow

```text
User Registration/Login
        ↓
JWT Token Generated
        ↓
Resume Uploaded
        ↓
Resume Stored and Parsed
        ↓
Candidate Profile Created
        ↓
Skills and Projects Extracted
        ↓
Question Bank Generated
        ↓
Mock Interview Started
        ↓
User Submits Answers
        ↓
AI Evaluates Answers
        ↓
Score and Feedback Generated
        ↓
User Reviews and Improves Answers
```

---

## 🗃️ Main Domain Models

### User

Stores user account and authentication information.

```text
User
├── id
├── fullName
├── email
├── password
├── role
└── createdAt
```

### Resume

Stores uploaded resume information.

```text
Resume
├── id
├── originalFileName
├── storageKey
├── contentType
├── fileSize
├── uploadedAt
└── user
```

### Candidate Profile

Stores structured information extracted from the resume.

```text
CandidateProfile
├── id
├── professionalTitle
├── totalExperienceMonths
├── professionalSummary
├── status
├── skills
└── projects
```

### Candidate Skill

```text
CandidateSkill
├── id
├── name
├── category
├── proficiency
├── yearsOfExperience
├── source
├── evidence
└── verified
```

### Candidate Project

```text
CandidateProject
├── id
├── projectName
├── role
├── responsibilities
├── achievements
├── startDate
├── endDate
└── technologies
```

### Interview Session

```text
InterviewSession
├── id
├── user
├── status
├── totalQuestions
├── answeredQuestions
├── startedAt
└── completedAt
```

### Interview Question

```text
InterviewQuestion
├── id
├── questionText
├── userAnswer
├── answered
├── answeredAt
├── score
└── aiFeedback
```

---

## 🌐 API Endpoints

### Authentication APIs

| Method | Endpoint             | Description                |
| ------ | -------------------- | -------------------------- |
| POST   | `/api/auth/register` | Register a new user        |
| POST   | `/api/auth/login`    | Authenticate a user        |
| GET    | `/api/auth/me`       | Get logged-in user details |

### Resume APIs

| Method | Endpoint                        | Description              |
| ------ | ------------------------------- | ------------------------ |
| POST   | `/api/resumes/upload`           | Upload a resume          |
| POST   | `/api/resumes/{resumeId}/parse` | Parse an uploaded resume |
| GET    | `/api/resumes/{resumeId}`       | Get resume details       |

### Candidate Profile APIs

| Method | Endpoint                                       | Description              |
| ------ | ---------------------------------------------- | ------------------------ |
| GET    | `/api/candidate-profiles/{profileId}`          | Get candidate profile    |
| PUT    | `/api/candidate-profiles/{profileId}`          | Update candidate profile |
| POST   | `/api/candidate-profiles/{profileId}/skills`   | Add candidate skills     |
| POST   | `/api/candidate-profiles/{profileId}/projects` | Add candidate projects   |

### Question Bank APIs

| Method | Endpoint                               | Description                      |
| ------ | -------------------------------------- | -------------------------------- |
| POST   | `/api/question-banks/generate`         | Generate a question bank         |
| GET    | `/api/question-banks`                  | Get all question banks           |
| GET    | `/api/question-banks/{questionBankId}` | Get question bank details        |
| GET    | `/api/questions/{questionId}`          | Get a question and answer        |
| PUT    | `/api/questions/{questionId}/feedback` | Improve an answer using feedback |

### Interview APIs

| Method | Endpoint                            | Description            |
| ------ | ----------------------------------- | ---------------------- |
| POST   | `/api/interview/start`              | Start a mock interview |
| POST   | `/api/interview/{sessionId}/answer` | Submit an answer       |
| GET    | `/api/interview/{sessionId}/result` | Get interview result   |
| GET    | `/api/interview/history`            | Get interview history  |

---

## 📥 Sample API Requests

### Register User

```json
{
  "fullName": "Shubham Singh Bhadouria",
  "email": "shubham@example.com",
  "password": "StrongPassword@123"
}
```

### Login User

```json
{
  "email": "shubham@example.com",
  "password": "StrongPassword@123"
}
```

### Start Interview

```json
{
  "candidateProfileId": "5b621db8-5f59-4d9c-8407-37ec646d9b19",
  "category": "JAVA",
  "difficulty": "INTERMEDIATE",
  "totalQuestions": 10
}
```

### Submit Answer

```json
{
  "questionId": "9ab74914-2cab-4699-b30e-edf83d5fd144",
  "answer": "HashMap stores data in key-value pairs and uses the hash code of the key to calculate the bucket index."
}
```

### Generate Question Bank

```json
{
  "candidateProfileId": "5b621db8-5f59-4d9c-8407-37ec646d9b19",
  "categories": [
    "JAVA",
    "SPRING_BOOT",
    "MICROSERVICES",
    "SQL",
    "REACT"
  ],
  "difficulty": "INTERMEDIATE",
  "questionsPerCategory": 10,
  "includeProjectQuestions": true
}
```

---

## ⚙️ Environment Variables

Create the required environment variables before running the project.

```env
DB_URL=jdbc:postgresql://localhost:5432/ai_interview_platform
DB_USERNAME=postgres
DB_PASSWORD=your_password

JWT_SECRET=your_secure_jwt_secret
JWT_EXPIRATION=86400000

GEMINI_API_KEY=your_gemini_api_key
OPENAI_API_KEY=your_openai_api_key

REDIS_HOST=localhost
REDIS_PORT=6379

FILE_UPLOAD_DIRECTORY=uploads
```

Do not commit real API keys or database passwords to GitHub.

---

## 🚀 Running the Project Locally

### Prerequisites

Make sure the following tools are installed:

* Java 17 or later
* Node.js 18 or later
* PostgreSQL
* Redis
* Docker
* Gradle
* Git

---

## ▶️ Backend Setup

Clone the repository:

```bash
git clone https://github.com/your-username/ai-interview-platform.git
```

Navigate to the backend directory:

```bash
cd ai-interview-platform/backend
```

Update the database and AI API configuration.

Run the application:

```bash
./gradlew bootRun
```

For Windows:

```bash
gradlew.bat bootRun
```

The backend application will run at:

```text
http://localhost:8080
```

---

## ▶️ Frontend Setup

Navigate to the frontend directory:

```bash
cd ai-interview-platform/frontend
```

Install dependencies:

```bash
npm install
```

Create a `.env` file:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Start the frontend:

```bash
npm run dev
```

The frontend application will run at:

```text
http://localhost:5173
```

---

## 🐳 Running With Docker

Build and start the complete application:

```bash
docker compose up --build
```

Stop the containers:

```bash
docker compose down
```

Services can include:

* Spring Boot backend
* React frontend
* PostgreSQL
* Redis

---

## 📖 Swagger API Documentation

After starting the backend, open:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger can be used to:

* Explore available APIs
* Test protected endpoints
* Upload resumes
* Generate question banks
* Start mock interviews
* Submit answers
* View interview results

For protected APIs, add the JWT token using the Swagger **Authorize** button.

```text
Bearer your-jwt-token
```

---

## 🔒 Security Implementation

The application uses JWT-based stateless authentication.

```text
Login Request
      ↓
Credentials Validated
      ↓
JWT Token Generated
      ↓
Token Sent to Frontend
      ↓
Frontend Stores Token
      ↓
Authorization Header Added
      ↓
JWT Filter Validates Token
      ↓
Security Context Updated
      ↓
Protected API Access Granted
```

Example request header:

```http
Authorization: Bearer your-jwt-token
```

---

## 🧪 Running Tests

Run all backend tests:

```bash
./gradlew test
```

Generate the test report:

```bash
./gradlew test jacocoTestReport
```

The test report will be available inside:

```text
build/reports/tests/test/index.html
```

---

## ⚠️ Exception Handling

The application uses centralized exception handling with `@RestControllerAdvice`.

Handled exceptions include:

* `ResourceNotFoundException`
* `UnauthorizedException`
* `BadRequestException`
* `FileStorageException`
* `AiProcessingException`
* Validation exceptions
* Generic server exceptions

Sample error response:

```json
{
  "timestamp": "2026-07-15T18:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Interview session was not found",
  "path": "/api/interview/session-id/result"
}
```

---

## 🗺️ Future Enhancements

* Voice-based interview answers
* Speech-to-text integration
* Video mock interviews
* Real-time interview timer
* WebSocket-based live interview sessions
* AI-generated follow-up questions
* Company-specific interview preparation
* Job-description-based question generation
* Resume ATS scoring
* Coding interview environment
* DSA problem evaluation
* Interview analytics dashboard
* Performance comparison across attempts
* Email-based interview reports
* AWS cloud deployment
* Kubernetes autoscaling
* Kafka-based asynchronous AI processing
* Multiple AI provider support
* Recruiter and administrator dashboards

---

## 🎯 Learning Outcomes

This project demonstrates practical experience with:

* Building enterprise applications using Spring Boot
* Designing RESTful APIs
* Implementing JWT authentication
* Integrating React with Spring Boot
* Designing relational database models
* Handling file uploads
* Parsing resumes using Generative AI
* Generating structured interview content
* Evaluating natural-language answers using AI
* Implementing centralized exception handling
* Using Redis for caching
* Containerizing applications with Docker
* Preparing applications for Kubernetes deployment
* Writing unit and integration tests
* Documenting APIs using Swagger

---

## 🤝 Contributing

Contributions, suggestions, and improvements are welcome.

1. Fork the repository
2. Create a new feature branch

```bash
git checkout -b feature/feature-name
```

3. Commit your changes

```bash
git commit -m "Add feature description"
```

4. Push the branch

```bash
git push origin feature/feature-name
```

5. Create a pull request

---

## 👨‍💻 Author

**Shubham Singh Bhadouria**

Java Backend and Full-Stack Developer with 3+ years of experience in building enterprise applications using Java, Spring Boot, Microservices, React, PostgreSQL, Redis, Kafka, Docker, Kubernetes, and AWS.

* LinkedIn: `https://www.linkedin.com/in/shubhambhadouria/`
* GitHub: `https://github.com/ShubhamBhadouria`

---

## ⭐ Support

If you found this project useful, consider giving the repository a star.

```text
⭐ Star the repository
🍴 Fork the project
🐛 Report issues
💡 Suggest new features
```

---

## 📜 License

This project is created for learning, portfolio development, and interview preparation purposes.

You may add an MIT License if you plan to make the repository open source.

---

## 📌 Project Status

```text
Backend Authentication       ✅ Completed
JWT Security                 ✅ Completed
Interview Session APIs       ✅ Completed
Answer Submission            ✅ Completed
AI Answer Evaluation         🚧 In Progress
Resume Upload                ✅ Completed
Resume Parsing               🚧 In Progress
Candidate Profile            🚧 In Progress
Question Bank Generation     🚧 In Progress
React Frontend               🚧 In Progress
Redis Integration            📅 Planned
Docker Deployment            📅 Planned
Kubernetes Deployment        📅 Planned
```
