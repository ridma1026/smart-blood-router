\# 🩸 Smart Emergency Blood \& Donor Routing System



A real-time blood donation matching platform connecting hospitals with voluntary donors.



\## 🚀 Features



\### Backend (Spring Boot)

\- ✅ JWT Authentication with role-based access (DONOR, HOSPITAL, ADMIN)

\- ✅ User registration and login

\- ✅ Donor profiles with blood group tracking

\- ✅ Hospital profiles with verification

\- ✅ Blood request creation with urgency levels

\- ✅ Smart donor matching algorithm with reliability scoring

\- ✅ 90-day cooldown period enforcement

\- ✅ Donor response tracking (accept/decline)

\- ✅ Real-time WebSocket notifications

\- ✅ Email notifications (configurable)

\- ✅ SMS notifications via Twilio (configurable)

\- ✅ Comprehensive blood type compatibility matrix



\### Tech Stack

\- \*\*Backend\*\*: Java 17, Spring Boot 4.0, Spring Security, JPA, WebSocket

\- \*\*Database\*\*: MySQL (XAMPP)

\- \*\*Authentication\*\*: JWT, BCrypt

\- \*\*Real-time\*\*: STOMP over WebSocket

\- \*\*Notifications\*\*: Email (SMTP), SMS (Twilio)



\## 🏗️ Project Structure

blood-donor-routing/

├── backend/

│ ├── src/main/java/com/bloodrouter/

│ │ ├── auth/ # Authentication controllers

│ │ ├── common/ # Shared entities and enums

│ │ ├── donor/ # Donor management

│ │ ├── hospital/ # Hospital management

│ │ ├── notification/ # Email, SMS, WebSocket services

│ │ ├── request/ # Blood request system

│ │ └── security/ # JWT security configuration

│ └── src/main/resources/

│ ├── templates/ # Email templates

│ └── application.properties

└── frontend/ (Coming soon)



text



\## 🔧 Setup Instructions



\### Prerequisites

\- Java 17

\- MySQL (XAMPP)

\- Maven



\### Database Setup

1\. Start MySQL in XAMPP

2\. Create database: `blood\_router`

3\. Update `application.properties` with your DB credentials



\### Run the Application

```bash

cd backend

./mvnw spring-boot:run

The server will start at http://localhost:8080



API Endpoints

Authentication

POST /auth/register - Register new user



POST /auth/login - Login and get JWT token



Donors

GET /donor/all - Get all donors



GET /donor/available?bloodGroup={group} - Find available donors



PUT /donor/availability - Update availability



Blood Requests

POST /api/blood-requests/create - Create new request



GET /api/blood-requests/{id}/matches - Find matching donors



POST /api/blood-requests/{id}/notify-donor/{donorId} - Notify donor



PUT /api/blood-requests/donor-response/{id} - Donor responds



🎯 Smart Matching Algorithm

The system ranks donors using:



Blood type compatibility (full medical matrix)



Urgency multiplier (CRITICAL: 1.5x, HIGH: 1.3x)



Reliability score (based on past behavior)



Recent donation penalty (90-day cooldown)



📱 Real-time Notifications

WebSocket endpoint: ws://localhost:8080/ws



/user/queue/notifications - Private donor notifications



/user/queue/donor-responses - Hospital updates



🔐 Environment Variables (Optional)

Create application-local.properties for sensitive data:



properties

\# Email

spring.mail.username=your-email@gmail.com

spring.mail.password=your-app-password



\# Twilio

twilio.account-sid=your-account-sid

twilio.auth-token=your-auth-token

twilio.phone-number=your-twilio-number

🚧 Coming Soon

React frontend with real-time dashboard



Location-based donor matching



Push notifications for mobile



Analytics dashboard



📝 License

MIT

