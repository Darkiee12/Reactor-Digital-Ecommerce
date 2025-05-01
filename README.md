# Electronic E-commerce Platform



## Project Overview
This repository contains a full-stack e-commerce application with separate frontend and backend services, containerized for easy deployment and development.

## Technology Stack
### Backend
- <img alt="Java" src="https://upload.wikimedia.org/wikipedia/vi/3/30/Java_programming_language_logo.svg" width="20" height="20"/> Java / <img alt="Spring" src="https://upload.wikimedia.org/wikipedia/commons/7/79/Spring_Boot.svg" width="20" height="20"/><img alt="Spring" src="https://spring.io/img/projects/spring-boot.svg" width="20" height="20"/><img alt="Spring" src="https://spring.io/img/projects/spring-data.svg" width="20" height="20"/><img alt="Spring" src="https://spring.io/img/projects/spring-security.svg" width="20" height="20"/>Spring Boot application (Spring WebFlux + R2DBC + Spring Security)
- Logging configured with Logback
- REST API endpoints

### Frontend
- <img alt="Preact" src="https://preactjs.com/app-icon.png" width="20" height="20"/> <img alt="Preact" src="https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/Typescript_logo_2020.svg/2048px-Typescript_logo_2020.svg.png" width="20" height="20"/> Preact with TypeScript
- <img alt="Spring" src="https://camo.githubusercontent.com/237e20be5fcfd8f7133f43d126fc49fb29dec7631679938bdd2ecb8cbb2a610e/68747470733a2f2f766974652e6465762f6c6f676f2e737667" width="20" height="20"/> Vite as build tool
- <img alt="Spring" src="https://avatars.githubusercontent.com/u/139895814?s=48&v=4" width="20" height="20"/> UI components with shadcn/ui styling


### Data Storage
- <img alt="Spring" src="https://www.postgresql.org/media/img/about/press/elephant.png" width="20" height="20"/> Database: PostgreSQL
- <img alt="Spring" src="https://dl.min.io/logo/Minio_logo_light/Minio_logo_light.svg" width="20" height="20"/> Data Storage: MinIO

## Project Structure
```
.
├── backend/               # Java/Spring Boot application
│   └── nashtech/          # Main application code
├── frontend/              # React/TypeScript client application
│   ├── public/            # Static assets
│   └── src/               # React components and logic
└── .devcontainer/         # Development container configuration
```

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java JDK 21 (for backend development)
- Node.js (for frontend development)

### Development Setup
1. Clone the repository
2. Start the development environment:
   ```bash
   docker-compose -f dev.compose.yml up
   ```
3. The frontend will be available at http://localhost:3000
4. The backend API will be available at http://localhost:8080

### Production Deployment
1. Build the production images:
   ```bash
   docker-compose build
   ```
2. Start the production services:
   ```bash
   docker-compose up -d
   ```

## Environment Configuration
- Backend: Configure `.env.dev` or `.env.prod` files
- Frontend: Configure `.env.dev` file for development settings

## Additional Resources
- API documentation is available in [`backend/rest.http`](backend/rest.http)
- Database schema samples in [`backend/sample.sql`](backend/sample.sql)

## Contributing
1. Create a feature branch
2. Make your changes
3. Submit a pull request

