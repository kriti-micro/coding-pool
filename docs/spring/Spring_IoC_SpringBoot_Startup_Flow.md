# 🌱 Spring IoC & Spring Boot — Object Creation & Startup Flow
> Topic: How Spring creates objects · ApplicationContext · context.xml · Auto-configuration · Bean Lifecycle
> Level: 8 Years Experience | MNC / Product Company Interviews

---

## 📌 Table of Contents

1. [Core Idea — What is IoC?](#1-core-idea--what-is-ioc)
2. [Classic Spring — applicationContext.xml](#2-classic-spring--applicationcontextxml)
3. [Java Config — @Configuration class](#3-java-config--configuration-class)
4. [Annotation-based — @Component + @Autowired](#4-annotation-based--component--autowired)
5. [Spring Boot — Full Startup Flow (8 Phases)](#5-spring-boot--full-startup-flow-8-phases)
6. [Phase Deep Dives with Code](#6-phase-deep-dives-with-code)
7. [Auto-Configuration — The Magic Explained](#7-auto-configuration--the-magic-explained)
8. [Bean Lifecycle — Complete Sequence](#8-bean-lifecycle--complete-sequence)
9. [context.xml vs Spring Boot — Side by Side](#9-contextxml-vs-spring-boot--side-by-side)
10. [Key Mental Model](#10-key-mental-model)
11. [Interview Questions & Answers](#11-interview-questions--answers)
12. [Quick Reference Cheat Sheet](#12-quick-reference-cheat-sheet)

---

## 1. Core Idea — What is IoC?

**IoC (Inversion of Control):** Control of object creation and lifecycle is handed over to the Spring container — not to the programmer.

**Without Spring (manual wiring):**
```java
// You create everything manually — tightly coupled
DataSource ds = new BasicDataSource();
ds.setUrl("jdbc:mysql://localhost/mydb");

UserRepository repo = new UserRepository(ds);
UserService service = new UserService(repo);
EmailService email = new EmailService();

// What if UserService also needs EmailService?
// You have to update every place that creates UserService
UserService service = new UserService(repo, email); // change everywhere!
```

**With Spring (IoC):**
```java
// You just declare what you need — Spring handles creation and wiring
@Service
public class UserService {
    private final UserRepository repo;
    private final EmailService email;

    public UserService(UserRepository repo, EmailService email) {
        // Spring injects these automatically — you never call new
        this.repo = repo;
        this.email = email;
    }
}
```

> 💡 **One-liner for interviews:**
> *"IoC means I declare what I need, Spring decides how and when to create and inject it. I never call `new` on my service classes."*

---

## 2. Classic Spring — applicationContext.xml

In legacy Spring (pre-Spring Boot, pre-annotations), all bean definitions lived in an XML file.

### The XML Config File

```xml
<!-- src/main/resources/applicationContext.xml -->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Bean 1: DataSource — no dependencies -->
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mydb"/>
        <property name="username" value="root"/>
        <property name="password" value="secret"/>
    </bean>

    <!-- Bean 2: Repository — depends on dataSource (injected by ref) -->
    <bean id="userRepository" class="com.example.repository.UserRepository">
        <property name="dataSource" ref="dataSource"/>
        <!-- ref="dataSource" means: inject the bean with id="dataSource" -->
    </bean>

    <!-- Bean 3: Service — constructor injection -->
    <bean id="userService" class="com.example.service.UserService">
        <constructor-arg ref="userRepository"/>
    </bean>

    <!-- Bean 4: Controller — depends on service -->
    <bean id="userController" class="com.example.controller.UserController">
        <property name="userService" ref="userService"/>
    </bean>

</beans>
```

### How to Bootstrap It

```java
public class MainApp {
    public static void main(String[] args) {

        // 1. Spring reads the XML and creates ApplicationContext (the container)
        ApplicationContext context =
            new ClassPathXmlApplicationContext("applicationContext.xml");

        // 2. All beans are now created and wired by Spring
        // 3. You just ask for what you need by id or type
        UserService service = context.getBean("userService", UserService.class);

        // 4. Use it — fully wired, ready to go
        service.registerUser("kriti@example.com");

        // 5. Close context to trigger @PreDestroy / destroy callbacks
        ((ConfigurableApplicationContext) context).close();
    }
}
```

### What Spring Does Internally When Reading XML

```
Step 1: Parse XML → collect all <bean> definitions
Step 2: Build dependency graph
         userController → userService → userRepository → dataSource
Step 3: Create beans in dependency order (leaves first):
         dataSource → userRepository → userService → userController
Step 4: Inject properties/constructor-args
Step 5: Call init-method (if specified)
Step 6: Application is ready
```

### Types of Injection in XML

```xml
<!-- Constructor injection (preferred — immutable) -->
<bean id="userService" class="com.example.UserService">
    <constructor-arg ref="userRepository"/>
    <constructor-arg value="100"/>  <!-- primitive value -->
</bean>

<!-- Setter injection (optional dependencies) -->
<bean id="userService" class="com.example.UserService">
    <property name="userRepository" ref="userRepository"/>
    <property name="maxRetries" value="3"/>
</bean>

<!-- List injection -->
<bean id="notificationService" class="com.example.NotificationService">
    <property name="handlers">
        <list>
            <ref bean="emailHandler"/>
            <ref bean="smsHandler"/>
        </list>
    </property>
</bean>
```

---

## 3. Java Config — @Configuration class

Spring 3+ introduced Java-based config — same concept as XML but type-safe and refactorable.

```java
@Configuration          // This class IS the config file (replaces applicationContext.xml)
@PropertySource("classpath:application.properties")  // Load properties
public class AppConfig {

    @Value("${db.url}")
    private String dbUrl;

    // Same as <bean id="dataSource" class="..."> in XML
    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(dbUrl);
        ds.setUsername("root");
        ds.setPassword("secret");
        return ds;
    }

    // Spring is smart: calling dataSource() here returns the SAME singleton bean
    // It does NOT create a new object — Spring intercepts the call
    @Bean
    public UserRepository userRepository() {
        return new UserRepository(dataSource());
    }

    @Bean
    public UserService userService() {
        return new UserService(userRepository());
    }
}
```

### How to Bootstrap with Java Config

```java
public class MainApp {
    public static void main(String[] args) {
        // Use AnnotationConfigApplicationContext instead of ClassPathXmlApplicationContext
        ApplicationContext context =
            new AnnotationConfigApplicationContext(AppConfig.class);

        UserService service = context.getBean(UserService.class);
        service.registerUser("kriti@example.com");
    }
}
```

> 💡 **Key insight:** When `userService()` calls `dataSource()`, Spring intercepts via CGLIB proxy — it returns the existing singleton from the container, not a new instance. That's why `@Configuration` classes are special.

---

## 4. Annotation-based — @Component + @Autowired

Spring 2.5+ allowed marking classes directly — Spring discovers them via classpath scanning.

```java
// Spring auto-detects this as a bean (no XML or @Bean needed)
@Repository   // specialization of @Component for data layer
public class UserRepository {

    @Autowired   // Spring injects matching bean by type
    private DataSource dataSource;

    public User findById(Long id) { /* ... */ }
}

@Service      // specialization of @Component for business logic
public class UserService {

    // PREFERRED: constructor injection (no @Autowired needed if single constructor in Spring 4.3+)
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}

@Controller   // specialization of @Component for web layer
public class UserController {
    @Autowired
    private UserService userService;
}
```

### Enable Component Scanning

**XML way:**
```xml
<context:component-scan base-package="com.example"/>
```

**Java Config way:**
```java
@Configuration
@ComponentScan(basePackages = "com.example")
public class AppConfig { }
```

**Spring Boot way:** `@SpringBootApplication` already includes `@ComponentScan` for the current package and all sub-packages.

### Stereotype Annotations — What's the Difference?

| Annotation | Layer | Extra Behaviour |
|---|---|---|
| `@Component` | Generic | Base annotation — no extra semantics |
| `@Service` | Business logic | Semantic clarity only — no technical difference |
| `@Repository` | Data access | Translates persistence exceptions → `DataAccessException` |
| `@Controller` | Web (MVC) | Handles HTTP requests, returns View name |
| `@RestController` | REST Web | `@Controller` + `@ResponseBody` combined |

---

## 5. Spring Boot — Full Startup Flow (8 Phases)

```
┌─────────────────────────────────────────────────────────────┐
│                    SPRING BOOT STARTUP                      │
└─────────────────────────────────────────────────────────────┘

  PHASE 1 ──► main() called
              SpringApplication.run(MyApp.class, args)
                          │
  PHASE 2 ──► SpringApplication created
              Detects app type (Servlet/Reactive/None)
              Loads ApplicationListeners from spring.factories
                          │
  PHASE 3 ──► ApplicationContext created
              AnnotationConfigServletWebServerApplicationContext (web)
              AnnotationConfigApplicationContext (non-web)
                          │
  PHASE 4 ──► Bean Registration (definitions only — no objects yet)
              ┌──────────────┬──────────────┬──────────────────┐
              │ @Component   │ @Configuration│ Auto-config      │
              │ @Service     │ @Bean methods │ META-INF/spring/ │
              │ @Repository  │ registered   │ *.imports files  │
              └──────────────┴──────────────┴──────────────────┘
              → All collected into BeanDefinitionRegistry
                          │
  PHASE 5 ──► Auto-configuration Conditions Evaluated
              @ConditionalOnClass       → Is driver on classpath?
              @ConditionalOnMissingBean → Did YOU define a DataSource?
              @ConditionalOnProperty    → Is spring.datasource.url set?
              → Spring decides which auto-configs to activate
                          │
  PHASE 6 ──► Bean Instantiation + Dependency Injection
              1. Create beans in dependency order
              2. Inject via constructor / setter / field
              3. Run @PostConstruct
              4. Run BeanPostProcessors (creates AOP proxies for
                 @Transactional, @Cacheable, @Async, @Retry)
                          │
  PHASE 7 ──► Runners execute
              ApplicationRunner.run()
              CommandLineRunner.run()
              → Custom startup logic (warm up cache, send health ping)
                          │
  PHASE 8 ──► Embedded Tomcat started
              "Started MyApp in 3.421 seconds"
              ✅ Application ready to serve requests
```

---

## 6. Phase Deep Dives with Code

### Phase 1 & 2 — `@SpringBootApplication` Unpacked

```java
@SpringBootApplication
// ↑ This single annotation = all three below combined:

// @Configuration        → this class can define @Bean methods
// @ComponentScan        → scan this package + sub-packages
// @EnableAutoConfiguration → trigger auto-config from classpath

public class MyApp {
    public static void main(String[] args) {
        // Returns the fully configured ApplicationContext
        ApplicationContext ctx = SpringApplication.run(MyApp.class, args);

        // You can even pull beans out manually (rarely needed):
        UserService svc = ctx.getBean(UserService.class);
    }
}
```

### Phase 3 — ApplicationContext Types

| Context Type | When Used |
|---|---|
| `AnnotationConfigServletWebServerApplicationContext` | Spring Boot web apps (default) |
| `AnnotationConfigReactiveWebServerApplicationContext` | Spring Boot WebFlux apps |
| `AnnotationConfigApplicationContext` | Non-web Spring Boot apps |
| `ClassPathXmlApplicationContext` | Legacy XML-based Spring |
| `AnnotationConfigApplicationContext` | Java config (non-Boot) |

### Phase 4 — BeanDefinition vs Bean Instance

```
BeanDefinition  =  Blueprint / Recipe
Bean Instance   =  The actual object (created later)

Spring first collects ALL blueprints, THEN creates objects.
This is why circular dependency errors happen at startup, not at runtime.
```

### Phase 5 — Auto-configuration Deep Dive

```java
// Inside spring-boot-autoconfigure.jar (Spring writes this, not you)
@AutoConfiguration
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataSource.class)  // only if YOU haven't defined one
    public DataSource dataSource(DataSourceProperties properties) {
        // Creates HikariCP pool from your application.yml values
        return properties.initializeDataSourceBuilder().build();
    }
}
```

**What triggers auto-config:**
```
You add to pom.xml:
    spring-boot-starter-data-jpa
          ↓
Classpath now has:
    DataSource.class ✓  HibernateJpaVendorAdapter.class ✓
          ↓
Spring Boot auto-configures:
    ✓ DataSource (HikariCP connection pool)
    ✓ EntityManagerFactory
    ✓ JpaTransactionManager
    ✓ Spring Data JPA repositories

You write ZERO config code.
```

### Phase 6 — Dependency Resolution Order

```java
// Spring builds a dependency graph and creates in correct order:

@Repository
public class UserRepository {
    // depends on: DataSource
}

@Service
public class UserService {
    // depends on: UserRepository, EmailService
    public UserService(UserRepository repo, EmailService email) { }
}

@RestController
public class UserController {
    // depends on: UserService
}

// Creation order Spring follows:
// 1. DataSource          (auto-configured, no deps)
// 2. EmailService        (no deps)
// 3. UserRepository      (needs DataSource)
// 4. UserService         (needs UserRepository + EmailService)
// 5. UserController      (needs UserService)
```

### Phase 6 — @PostConstruct + AOP Proxies

```java
@Service
public class UserService {
    private final UserRepository repo;
    private Map<Long, User> cache;

    public UserService(UserRepository repo) {
        this.repo = repo;
        // DON'T initialize cache here — repo may not be fully ready
    }

    @PostConstruct   // runs AFTER injection, BEFORE app serves traffic
    public void init() {
        // Safe to use repo here — all dependencies are injected
        this.cache = repo.findAllActive()
                         .stream()
                         .collect(Collectors.toMap(User::getId, u -> u));
        log.info("UserService cache loaded: {} users", cache.size());
    }

    @PreDestroy   // runs when context is closing
    public void cleanup() {
        cache.clear();
        log.info("UserService cache cleared");
    }
}
```

### How AOP Proxies Work (BeanPostProcessor magic)

```java
// You write this:
@Service
public class PaymentService {

    @Transactional   // ← Spring sees this at startup
    public void processPayment(Order order) {
        // your logic
    }
}

// What Spring actually puts in the ApplicationContext:
// NOT your PaymentService — but a PROXY that wraps it:

class PaymentService$$SpringCGLIB$$0 extends PaymentService {

    @Override
    public void processPayment(Order order) {
        // 1. Open transaction
        TransactionStatus tx = transactionManager.getTransaction(...);
        try {
            // 2. Call your real method
            super.processPayment(order);
            // 3. Commit
            transactionManager.commit(tx);
        } catch (RuntimeException e) {
            // 4. Rollback
            transactionManager.rollback(tx);
            throw e;
        }
    }
}

// Same happens for @Cacheable, @Async, @Retry, @CircuitBreaker
```

> ⚠️ **Common interview trap:** Self-invocation breaks AOP!
> ```java
> // BROKEN — calling processPayment() from within the same class
> // bypasses the proxy → @Transactional has NO effect
> public void placeOrder(Order order) {
>     this.processPayment(order);  // 'this' = real object, not proxy!
> }
> ```

### Phase 7 — Runners

```java
// Use for: cache warmup, health checks, data migration, startup notifications

@Component
public class StartupRunner implements ApplicationRunner {

    @Autowired
    private CacheService cacheService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("App started — warming up cache...");
        cacheService.warmUp();
        log.info("Cache ready. Serving traffic.");
    }
}

// CommandLineRunner — simpler, gets raw String[] args
@Component
public class DataMigrationRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (Arrays.asList(args).contains("--migrate")) {
            migrationService.runPendingMigrations();
        }
    }
}
```

---

## 7. Auto-Configuration — The Magic Explained

### How Spring Boot Discovers Auto-Configs

```
Your app starts
      ↓
Spring Boot scans every JAR on classpath for:
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
      ↓
Each entry = a class like DataSourceAutoConfiguration, KafkaAutoConfiguration, etc.
      ↓
Spring evaluates @Conditional annotations on each class
      ↓
Activates only the ones whose conditions are met
      ↓
Beans from activated auto-configs are added to the container
```

### The Conditional Annotations

| Annotation | Meaning |
|---|---|
| `@ConditionalOnClass` | Activate only if this class exists on classpath |
| `@ConditionalOnMissingClass` | Activate only if this class is NOT on classpath |
| `@ConditionalOnBean` | Activate only if this bean already exists |
| `@ConditionalOnMissingBean` | Activate only if this bean does NOT exist (your custom one wins) |
| `@ConditionalOnProperty` | Activate only if this property is set in yml/properties |
| `@ConditionalOnWebApplication` | Activate only in web context |
| `@ConditionalOnExpression` | Activate based on SpEL expression |

### How to Override Auto-Configuration (Your Bean Wins)

```java
// Spring Boot auto-configures a DataSource if you don't define one.
// But if you define your own — auto-config backs off:

@Configuration
public class MyDataSourceConfig {

    @Bean   // Your custom DataSource — Spring Boot's auto-config sees this
            // and @ConditionalOnMissingBean skips auto-creation
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://prod-server:3306/mydb");
        ds.setMaximumPoolSize(50);
        ds.setConnectionTimeout(30000);
        return ds;
    }
}
```

### How to Disable Auto-Configuration

```java
// Exclude specific auto-configs:
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    SecurityAutoConfiguration.class
})
public class MyApp { }

// Or in application.yml:
// spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

---

## 8. Bean Lifecycle — Complete Sequence

```
┌─────────────────────────────────────────────────────────────┐
│                    BEAN LIFECYCLE                           │
└─────────────────────────────────────────────────────────────┘

1. BeanDefinition loaded
   (Spring reads @Component / XML / @Bean method)
         ↓
2. BeanFactory creates instance
   (constructor called)
         ↓
3. Dependency Injection
   (constructor args / @Autowired / @Value injected)
         ↓
4. BeanNameAware.setBeanName()     ← if implemented
5. BeanFactoryAware.setBeanFactory() ← if implemented
6. ApplicationContextAware.setApplicationContext() ← if implemented
         ↓
7. BeanPostProcessor.postProcessBeforeInitialization()
   (custom pre-init logic)
         ↓
8. @PostConstruct method runs
         ↓
9. InitializingBean.afterPropertiesSet() ← if implemented
         ↓
10. Custom init-method (if configured)
          ↓
11. BeanPostProcessor.postProcessAfterInitialization()
    ← THIS is where AOP proxies are created
    ← @Transactional, @Cacheable, @Async proxies happen here
          ↓
12. ✅ BEAN IS READY — stored in ApplicationContext
    Used by application to serve requests
          ↓
    ... application runs ...
          ↓
13. ApplicationContext.close() called (app shutting down)
          ↓
14. @PreDestroy method runs
          ↓
15. DisposableBean.destroy() ← if implemented
          ↓
16. Custom destroy-method (if configured)
          ↓
17. Bean removed from context — GC eligible
```

### Code Example — Full Lifecycle

```java
@Service
public class ConnectionPoolService
        implements InitializingBean, DisposableBean, ApplicationContextAware {

    private ApplicationContext context;
    private ConnectionPool pool;

    // Step 3 — DI
    @Autowired
    private DataSourceConfig config;

    // Step 6
    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        this.context = ctx;
        log.info("ApplicationContext injected");
    }

    // Step 8 — @PostConstruct (preferred way)
    @PostConstruct
    public void init() {
        pool = new ConnectionPool(config.getUrl(), config.getPoolSize());
        log.info("Pool initialized with {} connections", config.getPoolSize());
    }

    // Step 9 — InitializingBean (alternative, less preferred)
    @Override
    public void afterPropertiesSet() {
        log.info("afterPropertiesSet called");
    }

    // Step 14 — @PreDestroy (preferred way)
    @PreDestroy
    public void cleanup() {
        pool.close();
        log.info("Pool closed");
    }

    // Step 15 — DisposableBean (alternative)
    @Override
    public void destroy() {
        log.info("destroy called");
    }
}
```

> 💡 **Best practice for interviews:** Use `@PostConstruct` and `@PreDestroy` — they're annotation-based and don't couple your class to Spring interfaces. `InitializingBean` / `DisposableBean` create Spring dependency.

---

## 9. context.xml vs Spring Boot — Side by Side

### Full Comparison: Same App, Three Ways

#### Way 1 — Legacy XML (Spring 3/4)

```xml
<!-- applicationContext.xml -->
<beans>
    <context:component-scan base-package="com.example"/>
    <context:property-placeholder location="classpath:db.properties"/>

    <!-- Manual DataSource -->
    <bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource"
          destroy-method="close">
        <property name="jdbcUrl" value="${db.url}"/>
        <property name="username" value="${db.user}"/>
        <property name="password" value="${db.pass}"/>
        <property name="maximumPoolSize" value="10"/>
    </bean>

    <!-- Manual EntityManagerFactory -->
    <bean id="entityManagerFactory"
          class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="packagesToScan" value="com.example.entity"/>
        <property name="jpaVendorAdapter">
            <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter"/>
        </property>
    </bean>

    <!-- Manual TransactionManager -->
    <bean id="transactionManager"
          class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
    </bean>

    <tx:annotation-driven transaction-manager="transactionManager"/>
</beans>
```

#### Way 2 — Java Config (Spring 4)

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.repository")
@ComponentScan("com.example")
public class AppConfig {

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost/mydb");
        ds.setUsername("root");
        ds.setMaximumPoolSize(10);
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.example.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }
}
```

#### Way 3 — Spring Boot (ZERO config code needed)

```yaml
# application.yml — this replaces ALL of the above XML/Java config
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
    hikari:
      maximum-pool-size: 10
      connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

```java
// This is ALL you need in Spring Boot — no config class required
@SpringBootApplication
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
// Spring Boot auto-configures: DataSource, EntityManagerFactory,
// JpaTransactionManager, Spring Data JPA repositories — everything.
```

### What auto-configures based on starter dependencies

| Starter added to pom.xml | Auto-configured beans |
|---|---|
| `spring-boot-starter-data-jpa` | DataSource, EntityManagerFactory, JpaTransactionManager |
| `spring-boot-starter-web` | DispatcherServlet, Tomcat, Jackson, MVC config |
| `spring-boot-starter-security` | SecurityFilterChain, UserDetailsService |
| `spring-boot-starter-data-redis` | RedisConnectionFactory, RedisTemplate |
| `spring-boot-starter-kafka` | KafkaTemplate, KafkaListenerContainerFactory |
| `spring-boot-starter-cache` | CacheManager (EhCache/Redis based on classpath) |
| `spring-boot-starter-actuator` | Health, Metrics, Info endpoints |

---

## 10. Key Mental Model

```
context.xml / @Configuration  =  The RECIPE
                                  (describes what beans exist and how to wire them)

ApplicationContext             =  The KITCHEN
                                  (reads recipe, creates everything, manages lifecycle)

@Autowired / Constructor DI    =  DELIVERY SERVICE
                                  (Spring delivers ready-made beans to where they're needed)

Spring Boot Auto-Config        =  PRE-WRITTEN RECIPES
                                  (for common ingredients — DB, Kafka, Redis, Security)
                                  (you only need to override when you want something custom)

application.yml                =  INGREDIENT CUSTOMIZATION
                                  (tell Spring Boot which DB URL, pool size, port to use)
```

### The One Rule

> **You describe WHAT you need. Spring figures out HOW and WHEN to create it. You never call `new` on your services, repositories, or controllers.**

---

## 11. Interview Questions & Answers

### ❓ Q1. What is the difference between BeanFactory and ApplicationContext?

> `BeanFactory` is the basic IoC container — lazy initialization, minimal features.
> `ApplicationContext` extends BeanFactory and adds: eager singleton initialization, event publishing, i18n support, AOP integration, `@PostConstruct`/`@PreDestroy` support.
> **In practice, always use `ApplicationContext` — `BeanFactory` is only for very constrained environments.**

### ❓ Q2. What happens if two beans have a circular dependency?

```java
@Service
public class A {
    @Autowired B b;  // A needs B
}

@Service
public class B {
    @Autowired A a;  // B needs A — circular!
}
```

> With **constructor injection**: Spring throws `BeanCurrentlyInCreationException` at startup — fails fast (good!).
> With **field/setter injection**: Spring can resolve it using a partially-created bean (risky — avoid circular deps).
> **Fix:** Redesign to remove the circular dependency, or use `@Lazy` on one injection point as a last resort.

### ❓ Q3. What is the default scope of a Spring bean?

> **Singleton** — one instance per `ApplicationContext`. Spring creates it once and returns the same instance for every `getBean()` or `@Autowired` injection.
> Other scopes: `prototype` (new instance each time), `request`, `session`, `application` (web only).

### ❓ Q4. How does `@Transactional` work internally?

> Spring wraps the bean in a **CGLIB proxy** during `BeanPostProcessor.postProcessAfterInitialization()`. When you call a `@Transactional` method, you're calling the proxy — which opens a transaction, delegates to your real method, then commits or rolls back. **This is why self-invocation (`this.method()`) bypasses the transaction — `this` refers to the real object, not the proxy.**

### ❓ Q5. What is the difference between `@Component` scan and auto-configuration?

> `@ComponentScan` finds **your classes** annotated with `@Component`, `@Service`, etc. inside your own packages.
> Auto-configuration finds **Spring Boot's pre-written configuration classes** inside JAR files on your classpath (via `META-INF/spring/*.imports`). These are activated conditionally based on what's on your classpath.

### ❓ Q6. How do you conditionally create a bean?

```java
@Bean
@ConditionalOnProperty(name = "feature.payment.enabled", havingValue = "true")
public PaymentGateway stripeGateway() {
    return new StripeGateway(apiKey);
}

@Bean
@Profile("prod")   // only in production profile
public DataSource prodDataSource() {
    return new HikariDataSource(prodConfig);
}

@Bean
@ConditionalOnMissingBean(CacheManager.class)  // only if no CacheManager defined yet
public CacheManager simpleCacheManager() {
    return new ConcurrentMapCacheManager("users", "orders");
}
```

### ❓ Q7. What is `@SpringBootApplication` made of?

```java
@SpringBootApplication
// = these three combined:
@Configuration           // can define @Bean methods
@ComponentScan           // scan current package + sub-packages
@EnableAutoConfiguration // trigger auto-config from classpath JARs
```

### ❓ Q8. When does `@PostConstruct` run vs constructor?

```
Constructor → Dependency Injection → @PostConstruct
```

> Constructor runs first — but dependencies are NOT yet injected at constructor time (for field/setter injection). `@PostConstruct` runs after all dependencies are injected — safe to use them there. **This is why initialization logic should be in `@PostConstruct`, not the constructor.**

---

## 12. Quick Reference Cheat Sheet

### Bean Creation Summary

| Approach | Config Location | Activation |
|---|---|---|
| XML | `applicationContext.xml` | `new ClassPathXmlApplicationContext("...")` |
| Java Config | `@Configuration` class | `new AnnotationConfigApplicationContext(Config.class)` |
| Component Scan | `@Component` on class | `@ComponentScan` or `@SpringBootApplication` |
| Auto-config | Spring Boot JAR | `@EnableAutoConfiguration` / `@SpringBootApplication` |

### Injection Types — When to Use

| Type | How | When |
|---|---|---|
| **Constructor** (preferred) | `public Service(Repo repo)` | Mandatory deps, immutable, easy to test |
| **Setter** | `@Autowired void setRepo(Repo r)` | Optional deps, can be reset |
| **Field** | `@Autowired Repo repo` | Quick prototypes only — avoid in production |

### Spring Boot Startup Sequence (One-liner Each)

```
1. main()               → SpringApplication.run() called
2. SpringApplication    → detects app type, loads listeners
3. ApplicationContext   → IoC container created
4. Bean registration    → blueprints collected (no objects yet)
5. Conditions evaluated → auto-config activated/skipped
6. Bean instantiation   → objects created, dependencies injected, proxies made
7. Runners              → custom startup logic
8. Embedded Tomcat      → app ready to serve traffic
```

### AOP Proxy Annotations (BeanPostProcessor creates proxies for all of these)

```java
@Transactional   // proxy opens/commits/rolls back transactions
@Cacheable       // proxy checks cache before calling real method
@Async           // proxy runs method in separate thread pool
@Retry           // proxy retries on exception
@CircuitBreaker  // proxy tracks failures, opens circuit
@PreAuthorize    // proxy checks security before method runs
```

### Common `application.yml` Properties

```yaml
spring:
  application:
    name: my-service

  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
    hikari:
      maximum-pool-size: 20

  jpa:
    hibernate:
      ddl-auto: validate      # none / validate / update / create / create-drop
    show-sql: false

  profiles:
    active: dev               # activate 'dev' profile

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  health:
    probes:
      enabled: true           # enables /actuator/health/liveness and /readiness
```

---

*Prepared from Claude AI session | Kriti Singh | 8 YOE Java + Spring Boot Developer*
*Topics: Spring IoC · ApplicationContext · context.xml · Auto-configuration · Bean Lifecycle · Spring Boot Startup*
