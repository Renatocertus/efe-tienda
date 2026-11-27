package com.efe.tienda.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    public static String MODO_ACTUAL = "DESCONOCIDO";

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/tienda_efe?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true}")
    private String mysqlUrl;

    @Value("${spring.datasource.username:root}")
    private String mysqlUsername;

    @Value("${spring.datasource.password:Root}")
    private String mysqlPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        HostPort hp = extraerHostYPuerto(mysqlUrl);
        log.info("🔍 [DIAGNÓSTICO INICIAL]: Verificando conexión a servidor MySQL en {}:{}...", hp.host, hp.port);

        boolean puertoAbierto = verificarPuerto(hp.host, hp.port, 2500);

        if (puertoAbierto) {
            try {
                HikariConfig mysqlConfig = new HikariConfig();
                mysqlConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
                mysqlConfig.setJdbcUrl(mysqlUrl);
                mysqlConfig.setUsername(mysqlUsername);
                mysqlConfig.setPassword(mysqlPassword);
                mysqlConfig.setConnectionTimeout(5000);
                // Clever Cloud plan gratuito permite máximo 5 conexiones totales
                // Limitamos Spring Boot a máximo 3 para no saturar el servidor
                mysqlConfig.setMaximumPoolSize(3);
                mysqlConfig.setMinimumIdle(1);
                mysqlConfig.setIdleTimeout(30000);
                mysqlConfig.setMaxLifetime(60000);
                mysqlConfig.setPoolName("MySQL-Cloud-Pool");

                HikariDataSource mysqlDs = new HikariDataSource(mysqlConfig);
                // Prueba real de conexión a MySQL
                try (Connection conn = mysqlDs.getConnection()) {
                    MODO_ACTUAL = "MYSQL";
                    log.info("==================================================================");
                    log.info("🟢 [MODO ACTIVO: MYSQL 8.0]");
                    log.info("✅ Conexión establecida exitosamente con el servidor MySQL en {}", hp.host);
                    log.info("👉 Las 5 tablas operarán con persistencia en MySQL en tiempo real.");
                    log.info("==================================================================");
                    return mysqlDs;
                }
            } catch (Exception e) {
                log.warn("⚠️ Servidor MySQL detectado en {}:{} pero no se pudo autenticar: {}", hp.host, hp.port, e.getMessage());
            }
        } else {
            log.warn("⚠️ No se pudo conectar a {}:{} (Host inalcanzable o sin conexión a internet).", hp.host, hp.port);
        }

        // ==================================================================
        // MODO RESPALDO: BASE DE DATOS EN MEMORIA (H2)
        // Permite que la aplicación levante y funcione al 100% en cualquier
        // computadora (como la de tu amigo o profesor) sin instalar MySQL.
        // ==================================================================
        MODO_ACTUAL = "H2_MEMORIA";
        log.info("==================================================================");
        log.info("🚀 [MODO DE RESPALDO ACTIVO: H2 DATABASE EN MEMORIA]");
        log.info("✅ Se ha activado la base de datos relacional interna autónoma.");
        log.info("👉 La aplicación web funcionará al 100% sin requerir MySQL instalado.");
        log.info("👉 Consola SQL disponible en: http://localhost:8080/h2-console");
        log.info("==================================================================");

        HikariConfig h2Config = new HikariConfig();
        h2Config.setDriverClassName("org.h2.Driver");
        h2Config.setJdbcUrl("jdbc:h2:mem:tienda_efe;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL");
        h2Config.setUsername("sa");
        h2Config.setPassword("");
        h2Config.setMaximumPoolSize(10);
        h2Config.setPoolName("H2-InMemory-Pool");

        return new HikariDataSource(h2Config);
    }

    private boolean verificarPuerto(String host, int puerto, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private HostPort extraerHostYPuerto(String jdbcUrl) {
        try {
            String clean = jdbcUrl.replace("jdbc:mysql://", "");
            int slashIdx = clean.indexOf('/');
            String hostPortPart = (slashIdx != -1) ? clean.substring(0, slashIdx) : clean;
            int colonIdx = hostPortPart.indexOf(':');
            if (colonIdx != -1) {
                String host = hostPortPart.substring(0, colonIdx);
                int port = Integer.parseInt(hostPortPart.substring(colonIdx + 1));
                return new HostPort(host, port);
            } else {
                return new HostPort(hostPortPart, 3306);
            }
        } catch (Exception e) {
            return new HostPort("localhost", 3306);
        }
    }

    private static class HostPort {
        final String host;
        final int port;
        HostPort(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}
