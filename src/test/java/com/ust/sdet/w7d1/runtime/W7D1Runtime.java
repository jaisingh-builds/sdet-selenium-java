package com.ust.sdet.w7d1.runtime;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class W7D1Runtime implements AutoCloseable {
    private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(90);
    private static final String CUSTOMER_EMAIL = "alice@shopkart.test";

    private final String baseUrl;
    private final String jdbcUrl;
    private final String databaseUsername;
    private final String databasePassword;
    private final String customerPassword;
    private final PostgreSQLContainer postgres;
    private final Process shopKartProcess;

    private W7D1Runtime(
            String baseUrl,
            String jdbcUrl,
            String databaseUsername,
            String databasePassword,
            String customerPassword,
            PostgreSQLContainer postgres,
            Process shopKartProcess
    ) {
        this.baseUrl = stripTrailingSlash(baseUrl);
        this.jdbcUrl = jdbcUrl;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.customerPassword = customerPassword;
        this.postgres = postgres;
        this.shopKartProcess = shopKartProcess;
    }

    public static W7D1Runtime start() {
        String mode = setting("w7d1.runtime", "W7D1_RUNTIME", "container");
        return switch (mode.toLowerCase()) {
            case "container" -> startContainerRuntime();
            case "external" -> startExternalRuntime();
            default -> throw new IllegalArgumentException("w7d1.runtime must be container or external");
        };
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String apiBase() {
        return baseUrl + "/api";
    }

    public String jdbcUrl() {
        return jdbcUrl;
    }

    public String databaseUsername() {
        return databaseUsername;
    }

    public String databasePassword() {
        return databasePassword;
    }

    public String customerEmail() {
        return CUSTOMER_EMAIL;
    }

    public String customerPassword() {
        return customerPassword;
    }

    @Override
    public void close() {
        if (shopKartProcess != null && shopKartProcess.isAlive()) {
            shopKartProcess.destroy();
            try {
                if (!shopKartProcess.waitFor(5, TimeUnit.SECONDS)) {
                    shopKartProcess.destroyForcibly();
                }
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                shopKartProcess.destroyForcibly();
            }
        }
        if (postgres != null) {
            postgres.stop();
        }
    }

    private static W7D1Runtime startContainerRuntime() {
        PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("shopkart_w7d1")
            .withUsername("shopkart_user")
            .withPassword("w7d1_database_password");
        postgres.start();

        Process process = null;
        try {
            Path shopKartRoot = Path.of(setting("shopkart.root", "SHOPKART_ROOT", "../sdet-retail-app"))
                .toAbsolutePath().normalize();
            Path frontend = shopKartRoot.resolve("frontend/dist/index.html");
            if (!Files.isRegularFile(frontend)) {
                throw new IllegalStateException(
                    "ShopKart frontend build is missing at " + frontend
                        + ". Run npm run build in sdet-retail-app first."
                );
            }

            int port = availablePort();
            String baseUrl = "http://127.0.0.1:" + port;
            String customerPassword = "W7D1@" + UUID.randomUUID().toString().replace("-", "");
            String databaseUrl = "postgresql://shopkart_user:w7d1_database_password@"
                + postgres.getHost() + ":" + postgres.getMappedPort(5432) + "/shopkart_w7d1";
            Path log = Path.of("target/w7d1-shopkart.log").toAbsolutePath();
            Files.createDirectories(log.getParent());

            ProcessBuilder builder = new ProcessBuilder(
                setting("node.binary", "NODE_BINARY", "node"),
                "backend/src/server.js"
            );
            builder.directory(shopKartRoot.toFile());
            builder.redirectErrorStream(true);
            builder.redirectOutput(log.toFile());
            Map<String, String> environment = builder.environment();
            environment.remove("ENV_FILE");
            environment.remove("DB_DIALECT");
            environment.remove("DB_HOST");
            environment.remove("DB_PORT");
            environment.remove("DB_NAME");
            environment.remove("DB_USER");
            environment.remove("DB_PASSWORD");
            environment.put("PORT", String.valueOf(port));
            environment.put("DATABASE_URL", databaseUrl);
            environment.put("SHOPKART_TOKEN_SECRET", UUID.randomUUID().toString().repeat(2));
            environment.put("SHOPKART_ALICE_PASSWORD", customerPassword);
            environment.put("SHOPKART_BOB_PASSWORD", "W7D1@Bob-" + UUID.randomUUID());
            environment.put("SHOPKART_CAROL_PASSWORD", "W7D1@Carol-" + UUID.randomUUID());
            environment.put("NODE_ENV", "w7d1-e2e");
            process = builder.start();
            awaitHealth(baseUrl, process, log, STARTUP_TIMEOUT);

            return new W7D1Runtime(
                baseUrl,
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                customerPassword,
                postgres,
                process
            );
        } catch (RuntimeException | IOException error) {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
            postgres.stop();
            throw new IllegalStateException("Could not start the W7D1 container runtime", error);
        }
    }

    private static W7D1Runtime startExternalRuntime() {
        String baseUrl = setting("shopkart.baseUrl", "SHOPKART_BASE_URL", "http://localhost:8080");
        String jdbcUrl = required("shopkart.jdbc.url", "SHOPKART_JDBC_URL");
        String username = required("shopkart.db.user", "SHOPKART_DB_USER");
        String password = required("shopkart.db.password", "SHOPKART_DB_PASSWORD");
        String customerPassword = required("shopkart.alice.password", "SHOPKART_ALICE_PASSWORD");
        awaitHealth(baseUrl, null, null, Duration.ofSeconds(20));
        return new W7D1Runtime(baseUrl, jdbcUrl, username, password, customerPassword, null, null);
    }

    private static void awaitHealth(String baseUrl, Process process, Path log, Duration timeout) {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(stripTrailingSlash(baseUrl) + "/api/health"))
            .timeout(Duration.ofSeconds(3))
            .GET()
            .build();
        long deadline = System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadline) {
            if (process != null && !process.isAlive()) {
                throw new IllegalStateException("ShopKart stopped during startup. See " + log);
            }
            try {
                if (client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode() == 200) {
                    return;
                }
            } catch (IOException ignored) {
                // The process is still starting.
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for ShopKart", error);
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for ShopKart", error);
            }
        }
        throw new IllegalStateException("ShopKart health endpoint did not become ready at " + baseUrl);
    }

    private static int availablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static String required(String property, String environment) {
        String value = setting(property, environment, "");
        if (value.isBlank()) {
            throw new IllegalStateException(
                "External W7D1 runtime requires system property " + property
                    + " or environment variable " + environment
            );
        }
        return value;
    }

    private static String setting(String property, String environment, String fallback) {
        String propertyValue = System.getProperty(property);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }
        String environmentValue = System.getenv(environment);
        return environmentValue == null || environmentValue.isBlank()
            ? fallback
            : environmentValue.trim();
    }

    private static String stripTrailingSlash(String value) {
        return value.replaceAll("/+$", "");
    }
}
