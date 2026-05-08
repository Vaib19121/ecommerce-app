# Graph Report - ecommerce-app  (2026-05-07)

## Corpus Check
- 82 files · ~11,697 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 381 nodes · 392 edges · 63 communities (21 shown, 42 thin omitted)
- Extraction: 83% EXTRACTED · 17% INFERRED · 0% AMBIGUOUS · INFERRED: 66 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `f8cbe60c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]
- [[_COMMUNITY_Community 28|Community 28]]
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 30|Community 30]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 33|Community 33]]
- [[_COMMUNITY_Community 34|Community 34]]
- [[_COMMUNITY_Community 35|Community 35]]
- [[_COMMUNITY_Community 36|Community 36]]
- [[_COMMUNITY_Community 37|Community 37]]
- [[_COMMUNITY_Community 38|Community 38]]
- [[_COMMUNITY_Community 39|Community 39]]
- [[_COMMUNITY_Community 40|Community 40]]
- [[_COMMUNITY_Community 41|Community 41]]
- [[_COMMUNITY_Community 42|Community 42]]
- [[_COMMUNITY_Community 43|Community 43]]
- [[_COMMUNITY_Community 44|Community 44]]
- [[_COMMUNITY_Community 45|Community 45]]
- [[_COMMUNITY_Community 46|Community 46]]
- [[_COMMUNITY_Community 47|Community 47]]
- [[_COMMUNITY_Community 48|Community 48]]
- [[_COMMUNITY_Community 49|Community 49]]
- [[_COMMUNITY_Community 50|Community 50]]
- [[_COMMUNITY_Community 51|Community 51]]
- [[_COMMUNITY_Community 52|Community 52]]

## God Nodes (most connected - your core abstractions)
1. `E-Commerce Application` - 17 edges
2. `ProductServiceImpl` - 11 edges
3. `ProductService` - 9 edges
4. `CategoryServiceImpl` - 8 edges
5. `ProductController` - 8 edges
6. `GlobalExceptionHandler` - 8 edges
7. `CartServiceImpl` - 8 edges
8. `CategoryController` - 7 edges
9. `CategoryService` - 7 edges
10. `JwtTokenProvider` - 7 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities (63 total, 42 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (11): AuthService, SecurityConfig, Build, code:bash (./mvnw clean install), JwtAuthFilter, JwtTokenProvider, OncePerRequestFilter, UserRepository (+3 more)

### Community 1 - "Community 1"
Cohesion: 0.06
Nodes (32): SwaggerConfig, API Documentation, API Endpoints, Authentication, Build & Test, Categories, code:block14 (src/main/java/com/ecommerce/), code:block15 (http://localhost:8080/actuator/health) (+24 more)

### Community 2 - "Community 2"
Cohesion: 0.12
Nodes (5): AuthController, CartController, CategoryController, ProductController, UserController

### Community 3 - "Community 3"
Cohesion: 0.08
Nodes (4): ProductService, ProductRepository, ProductReviewRepository, ProductServiceImpl

### Community 4 - "Community 4"
Cohesion: 0.12
Nodes (5): CartService, Cart, CartItemRepository, CartRepository, CartServiceImpl

### Community 5 - "Community 5"
Cohesion: 0.13
Nodes (4): CategoryService, DataInitializer, CategoryRepository, CategoryServiceImpl

### Community 7 - "Community 7"
Cohesion: 0.18
Nodes (11): 1. Clone the Repository, 2. Configure Database, 3. Build the Application, 4. Run the Application, code:bash (git clone <repository-url>), code:bash (# No additional configuration needed), code:yaml (spring:), code:bash (./mvnw clean install) (+3 more)

### Community 9 - "Community 9"
Cohesion: 0.2
Nodes (4): BusinessException, ResourceNotFoundException, UnauthorizedException, RuntimeException

### Community 10 - "Community 10"
Cohesion: 0.2
Nodes (10): Add to Cart (requires authentication), code:bash (curl -X POST http://localhost:8080/api/auth/login \), code:json ({), code:bash (curl -X GET "http://localhost:8080/api/products?page=0&size=), code:bash (curl -X POST http://localhost:8080/api/cart/items \), code:bash (curl -X POST http://localhost:8080/api/auth/register \), Get Products (with pagination), Login (+2 more)

### Community 11 - "Community 11"
Cohesion: 0.22
Nodes (8): code:block1 (ecommerce/), `common/` for shared code, DTOs are per-feature, E-Commerce Spring Boot — Feature-Based Folder Structure, Each feature is self-contained, Enums live in the model folder, Features Overview, Key Principles

### Community 16 - "Community 16"
Cohesion: 0.33
Nodes (5): Getting Started, Guides, Maven Parent overrides, Read Me First, Reference Documentation

## Knowledge Gaps
- **57 isolated node(s):** `CategoryDto`, `CategoryRequest`, `Category`, `RegisterRequest`, `LoginRequest` (+52 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **42 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Build` connect `Community 0` to `Community 1`?**
  _High betweenness centrality (0.132) - this node is a cross-community bridge._
- **Why does `E-Commerce Application` connect `Community 1` to `Community 10`, `Community 7`?**
  _High betweenness centrality (0.125) - this node is a cross-community bridge._
- **Why does `Build & Test` connect `Community 1` to `Community 0`?**
  _High betweenness centrality (0.120) - this node is a cross-community bridge._
- **What connects `CategoryDto`, `CategoryRequest`, `Category` to the rest of the system?**
  _57 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.08 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.06 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.12 - nodes in this community are weakly interconnected._