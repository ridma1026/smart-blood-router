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

└── frontend/ 





Location-based donor matching



Push notifications for mobile



Analytics dashboard



📝 License

MIT

