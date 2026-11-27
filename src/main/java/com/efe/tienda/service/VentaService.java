package com.efe.tienda.service;

import com.efe.tienda.dto.ItemVentaDTO;
import com.efe.tienda.dto.VentaRequestDTO;
import com.efe.tienda.model.*;
import com.efe.tienda.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final MovimientoStockRepository movimientoStockRepository;

    public VentaService(PedidoRepository pedidoRepository,
                        PedidoDetalleRepository pedidoDetalleRepository,
                        ProductoRepository productoRepository,
                        ClienteRepository clienteRepository,
                        ComprobanteRepository comprobanteRepository,
                        MovimientoStockRepository movimientoStockRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoDetalleRepository = pedidoDetalleRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.movimientoStockRepository = movimientoStockRepository;
    }

    @Transactional
    public Pedido procesarVenta(VentaRequestDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un producto.");
        }

        // 1. Registrar o recuperar el Cliente
        Cliente cliente = clienteRepository.findByNumeroDocumento(dto.getNumeroDocumento())
                .orElseGet(() -> {
                    Cliente nuevo = new Cliente();
                    nuevo.setTipoDocumento(dto.getTipoDocumento() != null ? dto.getTipoDocumento() : "DNI");
                    nuevo.setNumeroDocumento(dto.getNumeroDocumento());
                    nuevo.setNombres(dto.getNombres());
                    nuevo.setApellidos(dto.getApellidos());
                    nuevo.setCorreo(dto.getCorreo());
                    nuevo.setTelefono(dto.getTelefono());
                    nuevo.setDireccion(dto.getDireccion());
                    return clienteRepository.save(nuevo);
                });

        // 2. Generar Código único de Pedido
        String codigoPedido = "PED-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        Pedido pedido = new Pedido();
        pedido.setCodigo(codigoPedido);
        pedido.setCliente(cliente);
        pedido.setMetodoEntrega(dto.getMetodoEntrega() != null ? dto.getMetodoEntrega() : "Delivery");
        pedido.setDireccionEnvio(dto.getDireccionEnvio() != null ? dto.getDireccionEnvio() : dto.getDireccion());
        pedido.setCostoEnvio(dto.getCostoEnvio() != null ? dto.getCostoEnvio() : BigDecimal.ZERO);
        pedido.setFechaEmision(LocalDateTime.now());
        pedido.setEstado("PAGO APROBADO");

        BigDecimal subtotalAcumulado = BigDecimal.ZERO;

        // 3. Procesar Items y Descontar Stock
        for (ItemVentaDTO itemDTO : dto.getItems()) {
            Producto producto = productoRepository.findById(itemDTO.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto ID " + itemDTO.getProductoId() + " no encontrado."));

            if (producto.getStock() < itemDTO.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para: " + producto.getNombre() + ". Disponible: " + producto.getStock());
            }

            int stockAnterior = producto.getStock();
            int nuevoStock = stockAnterior - itemDTO.getCantidad();
            producto.setStock(nuevoStock);
            productoRepository.save(producto);

            // Registrar movimiento de salida en kardex
            MovimientoStock kardex = new MovimientoStock();
            kardex.setProducto(producto);
            kardex.setTipo("SALIDA");
            kardex.setCantidad(itemDTO.getCantidad());
            kardex.setStockAnterior(stockAnterior);
            kardex.setStockPosterior(nuevoStock);
            kardex.setMotivo("Venta realizada en " + codigoPedido);
            kardex.setUsuarioResponsable("Sistema / Venta Web");
            kardex.setFechaMovimiento(LocalDateTime.now());
            movimientoStockRepository.save(kardex);

            // Calcular precio considerando si tiene oferta activa
            BigDecimal precioEfectivo = (producto.getOferta() != null && producto.getOferta().compareTo(BigDecimal.ZERO) > 0)
                    ? producto.getOferta()
                    : producto.getPrecio();

            BigDecimal subtotalLinea = precioEfectivo.multiply(BigDecimal.valueOf(itemDTO.getCantidad()));
            subtotalAcumulado = subtotalAcumulado.add(subtotalLinea);

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(itemDTO.getCantidad());
            detalle.setPrecioUnitario(precioEfectivo);
            detalle.setSubtotal(subtotalLinea);
            pedido.getDetalles().add(detalle);
        }

        // 4. Cálculos tributarios (IGV 18% Perú)
        BigDecimal baseImponible = subtotalAcumulado.divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);
        BigDecimal igv = subtotalAcumulado.subtract(baseImponible);
        BigDecimal totalConEnvio = subtotalAcumulado.add(pedido.getCostoEnvio());

        pedido.setSubtotal(baseImponible);
        pedido.setIgv(igv);
        pedido.setTotal(totalConEnvio);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 5. Facturación Electrónica (Generación de Comprobante: Boleta o Factura)
        String tipoDoc = (dto.getTipoComprobante() != null && dto.getTipoComprobante().equalsIgnoreCase("FACTURA"))
                ? "FACTURA" : "BOLETA";
        String serie = tipoDoc.equals("FACTURA") ? "F001" : "B001";
        long correlativo = comprobanteRepository.countByTipoComprobanteAndSerie(tipoDoc, serie) + 1;
        String numeroComprobante = String.format("%08d", correlativo);

        Comprobante comprobante = new Comprobante();
        comprobante.setPedido(pedidoGuardado);
        comprobante.setTipoComprobante(tipoDoc);
        comprobante.setSerie(serie);
        comprobante.setNumero(numeroComprobante);
        comprobante.setClienteDocumento(dto.getNumeroDocumento());
        comprobante.setClienteRazonSocial(
                tipoDoc.equals("FACTURA") && dto.getRazonSocial() != null && !dto.getRazonSocial().isBlank()
                        ? dto.getRazonSocial()
                        : dto.getNombres() + " " + dto.getApellidos()
        );
        comprobante.setClienteDireccion(dto.getDireccion());
        comprobante.setSubtotal(baseImponible);
        comprobante.setIgv(igv);
        comprobante.setTotal(totalConEnvio);
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setEstadoSunat("ACEPTADO");

        comprobanteRepository.save(comprobante);
        pedidoGuardado.setComprobante(comprobante);

        return pedidoGuardado;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaEmisionDesc();
    }

    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaEmisionDesc(clienteId);
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Transactional
    public Pedido cambiarEstado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + id));
        pedido.setEstado(nuevoEstado.toUpperCase());
        return pedidoRepository.save(pedido);
    }

    public List<Comprobante> listarComprobantes() {
        return comprobanteRepository.findAll();
    }
}
