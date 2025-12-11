# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Prompt Evaluation System** - A self-directed learning platform for systematically evaluating and improving AI prompt quality. Users can store prompts, execute them against AI models, evaluate results, and receive AI-powered analysis for continuous improvement.

- **Group**: kr.co.ansandy
- **Version**: 0.0.1-SNAPSHOT
- **Java Version**: 21
- **Spring Boot**: 4.0.0

## Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Clean build
./gradlew clean build
```

### Testing
```bash
# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "kr.co.ansandy.prompt_ev.PromptEvApplicationTests"

# Run tests with coverage
./gradlew test jacocoTestReport
```

### Database
- **Database**: SQLite (file: `prompt_ev.db` in project root)
- **Schema management**: Hibernate `ddl-auto: update` (auto-generates schema)
- **Location**: Local file-based database for data privacy

## Architecture

### Package Structure
```
kr.co.ansandy.prompt_ev/
├── config/          # Configuration classes (JPA auditing, property loading)
├── controller/      # REST controllers and web endpoints
├── entity/          # JPA entities
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic (to be implemented)
└── dto/             # Data transfer objects (to be implemented)
```

### Data Model

The system uses a hierarchical entity structure with `BaseEntity` providing audit fields:

**BaseEntity** (MappedSuperclass)
- Provides `createdAt` and `updatedAt` timestamps
- Uses JPA auditing (`@EntityListeners(AuditingEntityListener.class)`)
- All domain entities extend this base class

**Core Entities** (see PROJECT_PLAN.md for full ERD):
1. **Prompt** - Stores prompt content, category, and metadata
2. **PromptExecution** - Records AI execution results (to be implemented)
3. **UserEvaluation** - User ratings and feedback (to be implemented)
4. **AIAnalysis** - AI-powered prompt analysis (to be implemented)

### Key Technical Details

**JPA Configuration**:
- JPA Auditing is enabled via `@EnableJpaAuditing` in `JpaAuditingConfig`
- All entities extending `BaseEntity` automatically track creation and modification times

**Database**:
- SQLite with Hibernate Community Dialects (`org.hibernate.community.dialect.SQLiteDialect`)
- Connection string: `jdbc:sqlite:prompt_ev.db`
- Schema auto-generation enabled for rapid development

**Property Management**:
- Uses `@PropertySource` to load from `properties/env.properties`
- Main config in `application.yaml`

## Implementation Status

### Completed (Phase 1 - MVP in progress)
- ✅ Basic project structure and Spring Boot setup
- ✅ SQLite database integration with Hibernate
- ✅ BaseEntity with JPA auditing
- ✅ Prompt entity (partial - needs relationships)
- ✅ PromptRepository interface
- ✅ Health check endpoint at `/`

### To Be Implemented (see PROJECT_PLAN.md Phase 1)
- Remaining entities: PromptExecution, UserEvaluation, AIAnalysis
- Entity relationships (@OneToMany, @ManyToOne mappings)
- Service layer with business logic
- Controller endpoints for CRUD operations
- Thymeleaf templates for UI
- Validation annotations on entities

## Development Guidelines

### Entity Development
- All entities must extend `BaseEntity` for automatic timestamp tracking
- Use Lombok annotations (`@Getter`, `@Setter`, `@NoArgsConstructor`) to reduce boilerplate
- Define relationships using JPA annotations (`@OneToMany`, `@ManyToOne`, etc.)
- Use `columnDefinition = "TEXT"` for large text fields (SQLite compatibility)

### Service Layer Pattern
Services should handle:
- Business logic and validation
- Transaction management
- Coordination between repositories
- Integration with external AI APIs (Phase 2)

### Controller Design
- Use RESTful conventions for API endpoints
- Return proper HTTP status codes
- Use DTOs for request/response payloads (not entities directly)
- Thymeleaf for server-side rendered pages

## Testing Strategy

- Unit tests for service layer logic
- Integration tests for repository operations
- Controller tests using `@WebMvcTest`
- Use JUnit 5 (`useJUnitPlatform()` configured)

## Future Integration Points

**AI API Integration (Phase 2)**:
- OpenAI API for prompt analysis
- Anthropic API as alternative
- API keys managed via environment variables or encrypted config
- Minimal data sent to external APIs (privacy-first approach)

**Planned Features (see PROJECT_PLAN.md)**:
- Phase 1: Core CRUD and user evaluation
- Phase 2: AI analysis integration
- Phase 3: Statistics dashboard and insights
- Phase 4: Templates, version control, advanced search

## Important Project Files

- **PROJECT_PLAN.md**: Comprehensive product requirements, data models, and development roadmap
- **build.gradle**: Project dependencies and build configuration
- **application.yaml**: Database and JPA configuration
- **prompt_ev.db**: SQLite database file (gitignored)