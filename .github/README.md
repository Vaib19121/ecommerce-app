# AI Assistant Configuration Guide

This directory contains AI assistant configuration files that help Claude and GitHub Copilot understand and work with this Spring Boot e-commerce project.

## Directory Structure

```
.github/
├── copilot-instructions.md          # Main project instructions for AI assistants
├── skills/                           # Specialized skills for specific tasks
│   └── spring-boot-ecommerce-setup/
│       └── SKILL.md                  # Project setup and architecture skill
└── instructions/                     # File-specific coding guidelines
    ├── controller.instructions.md    # REST controller patterns
    ├── service.instructions.md       # Service layer patterns
    ├── repository.instructions.md    # Repository patterns
    ├── entity.instructions.md        # JPA entity patterns
    ├── dto.instructions.md           # DTO patterns
    └── mapper.instructions.md        # MapStruct mapper patterns
```

## What These Files Do

### 1. Main Instructions (`copilot-instructions.md`)
- **Always loaded** by AI assistants when working in this project
- Provides high-level overview of the architecture
- Defines coding standards and conventions
- Explains project structure and patterns

### 2. Skills (`skills/spring-boot-ecommerce-setup/SKILL.md`)
- **Invoked on-demand** when you ask questions like:
  - "How do I set up this project?"
  - "Explain the authentication flow"
  - "How do I add a new feature?"
- Comprehensive setup guide and troubleshooting
- Architecture deep-dive
- Step-by-step workflows

### 3. File Instructions (`instructions/*.instructions.md`)
- **Automatically loaded** when working with specific file types
- Controller instructions apply to `*Controller.java` files
- Service instructions apply to `*Service.java` files
- And so on...
- Provides templates and best practices specific to that layer

## How to Use

### For Developers

Simply work on your code as usual. When you use AI assistants:
- They will automatically follow the project conventions
- Ask questions like: "How do I add a new product review feature?"
- The AI will use these instructions to generate code matching your project's patterns

### Asking for Help

**Setup and Architecture:**
```
"How do I set up this Spring Boot project?"
"Explain the authentication flow in this app"
"How is the project structured?"
```

**Adding New Features:**
```
"Add a review entity with rating and comment"
"Create an order management feature"
"Add wishlist functionality for customers"
```

**Specific Patterns:**
```
"Show me how to create a new controller"
"How do I add pagination to a service?"
"Create a mapper for the Order entity"
```

**Troubleshooting:**
```
"Why isn't my mapper generating?"
"How do I fix JWT authentication issues?"
"My repository method isn't working"
```

## Instruction File Scopes

| File | Applies To | When Loaded |
|------|-----------|-------------|
| `copilot-instructions.md` | Entire project | Always |
| `spring-boot-ecommerce-setup/SKILL.md` | Setup/architecture questions | On-demand |
| `controller.instructions.md` | `*Controller.java` | When viewing/editing controllers |
| `service.instructions.md` | `*Service.java`, `*ServiceImpl.java` | When viewing/editing services |
| `repository.instructions.md` | `*Repository.java` | When viewing/editing repositories |
| `entity.instructions.md` | `model/*.java` | When viewing/editing entities |
| `dto.instructions.md` | `dto/*.java` | When viewing/editing DTOs |
| `mapper.instructions.md` | `mapper/*Mapper.java` | When viewing/editing mappers |

## Quick Reference: Project Patterns

### Adding a Complete Feature Module

1. **Create entity** (see `entity.instructions.md`)
2. **Create repository** (see `repository.instructions.md`)
3. **Create DTOs** (see `dto.instructions.md`)
4. **Create mapper** (see `mapper.instructions.md`)
5. **Create service interface + impl** (see `service.instructions.md`)
6. **Create controller** (see `controller.instructions.md`)

Just ask: *"Create a complete review feature with rating and comment"*

### Common Tasks

**Add endpoint:**
```
"Add a GET endpoint to find products by price range"
```

**Add validation:**
```
"Add validation to ensure email is unique when creating users"
```

**Add search:**
```
"Add full-text search to products by name and description"
```

**Add relationship:**
```
"Add a one-to-many relationship between Product and Review"
```

## Updating These Instructions

If you want to modify or extend these instructions:

1. **Main conventions** → Edit `copilot-instructions.md`
2. **Setup/architecture** → Edit `skills/spring-boot-ecommerce-setup/SKILL.md`
3. **Layer-specific patterns** → Edit respective `instructions/*.instructions.md`

After editing, the AI will automatically use the updated instructions on your next interaction.

## Benefits

✅ **Consistent Code**: AI generates code matching your project's patterns  
✅ **Faster Development**: No need to explain patterns every time  
✅ **Best Practices**: Built-in guidance for Spring Boot patterns  
✅ **Less Context Switching**: Instructions automatically loaded per file type  
✅ **Onboarding**: New AI sessions understand the project immediately  

## Examples

### Before (without instructions):
```
You: "Add a product endpoint"
AI: Creates inconsistent code with different naming, no pagination, wrong response format
```

### After (with instructions):
```
You: "Add a product endpoint"
AI: Creates properly structured controller with:
    - Correct annotations (@RestController, @PreAuthorize, @Operation)
    - Pagination support (page, size, sortBy, sortDir)
    - ApiResponse wrapper
    - Swagger documentation
    - Follows naming conventions
```

## Troubleshooting

**AI not following patterns?**
- Instructions might not be loaded yet
- Try asking explicitly: "Follow the project's controller pattern"
- Ensure you're editing the right file type

**Want different patterns?**
- Edit the relevant instruction file
- Be specific about what you want changed

**Need more examples?**
- Check existing code in the project
- Look at the templates in instruction files
- Ask: "Show me examples from this codebase"

## Related Files

- `pom.xml` - Maven dependencies
- `application.yml` - Configuration
- `README.md` - General project documentation
- `ecommerce-springboot-structure.md` - Project structure reference

## Questions?

Ask the AI assistant:
- "Explain the instruction files in this project"
- "How do I modify the coding standards?"
- "Show me the controller template"
- "What patterns does this project use?"
