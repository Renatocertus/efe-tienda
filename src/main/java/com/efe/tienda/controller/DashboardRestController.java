package com.efe.tienda.controller;

import com.efe.tienda.config.DataSourceConfig;
import com.efe.tienda.dto.DashboardResponseDTO;
import com.efe.tienda.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    private final DashboardService dashboardService;

    public DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResponseDTO> obtenerResumen() {
        return ResponseEntity.ok(dashboardService.obtenerResumen());
    }

    @GetMapping("/estado-conexion")
    public ResponseEntity<Map<String, String>> obtenerEstadoConexion() {
        String modo = DataSourceConfig.MODO_ACTUAL;
        String desc = "MYSQL".equalsIgnoreCase(modo)
                ? "Conectado a MySQL 8.0 en tiempo real (localhost:3306)"
                : "Operando con Base de Datos H2 en Memoria (Modo Autónomo Resiliente)";
        return ResponseEntity.ok(Map.of(
                "modo", modo,
                "descripcion", desc
        ));
    }
}
