package com.uade.tpo.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.CarritoExpiradoException;
import com.uade.tpo.demo.exceptions.ItemCarritoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.exceptions.UsuarioNotFoundException;
import com.uade.tpo.demo.repository.AsientoRepository;
import com.uade.tpo.demo.repository.CarritoRepository;
import com.uade.tpo.demo.repository.ItemCarritoRepository;
import com.uade.tpo.demo.repository.ProductoRepository;
import com.uade.tpo.demo.repository.UsuarioRepository;
import com.uade.tpo.demo.util.PrecioUtils;

import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.dto.CarritoResponse;
import com.uade.tpo.demo.entity.dto.ItemCarritoResponse;
import com.uade.tpo.demo.repository.FuncionRepository;
import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.ItemCarritoInvalidoException;
import com.uade.tpo.demo.exceptions.SeleccionButacaInvalidaException;
import com.uade.tpo.demo.exceptions.CantidadInvalidaException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;

@Service
public class CarritoServiceIMPL implements CarritoService {
   @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private ItemCarritoRepository itemCarritoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private AsientoRepository asientoRepository;
    @Autowired
    private FuncionRepository funcionRepository;

    @Override
    @Transactional
    public Carrito obtenerCarritoPorUsuario(Long usuarioId) throws UsuarioNotFoundException {
        Carrito carrito = this.buscarOCrear(usuarioId);
        this.limpiarSiVencio(carrito);
        return carrito;
    }

    // Trae el carrito bloqueado (ver CarritoRepository.findForUpdateByUsuarioId) o lo crea si no existe.
    private Carrito buscarOCrear(Long usuarioId) throws UsuarioNotFoundException {
        Optional<Carrito> carritoExistente = carritoRepository.findForUpdateByUsuarioId(usuarioId);

        if (carritoExistente.isPresent()) {
            return carritoExistente.get();
        } else {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(UsuarioNotFoundException::new);

            Carrito nuevoCarrito = new Carrito();
            nuevoCarrito.setUsuario(usuario);

            return carritoRepository.save(nuevoCarrito);
        }
    }

    // Si el timer ya vencio y el scheduler todavia no paso, se vacia aca. Devuelve true si lo vacio.
    private boolean limpiarSiVencio(Carrito carrito) {
        if (carrito.getExpiraEn() != null && carrito.getExpiraEn().isBefore(java.time.LocalDateTime.now())) {
            this.limpiar(carrito, true);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Carrito agregarItem(Long usuarioId, Long productoId, Long asientoId, Long funcionId, Integer cantidad)
        throws UsuarioNotFoundException, ProductoNotFoundException, AsientoNotFoundException,
                   StockInsuficienteException, FuncionNotFoundException, SeleccionButacaInvalidaException,
                   ItemCarritoInvalidoException, CantidadInvalidaException, CarritoExpiradoException {

        Carrito carrito = this.buscarOCrear(usuarioId);

        // Si el carrito vencio recien, se vacia y se avisa en vez de agregar en silencio sobre un carrito
        // que perdio sus items. El siguiente intento ya entra normal (expiraEn quedo en null).
        if (this.limpiarSiVencio(carrito)) {
            throw new CarritoExpiradoException();
        }

        // Solo aplica a productos (butaca fuerza cantidad a 1). Sin esto, null o negativo rompian todo.
        if (productoId != null && (cantidad == null || cantidad <= 0)) {
            throw new CantidadInvalidaException();
        }

        if (productoId != null) {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(ProductoNotFoundException::new);

            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new ProductoNotFoundException();
            }
            
            Optional<ItemCarrito> itemExistente = itemCarritoRepository.findByCarritoAndProducto(carrito, producto);

            if (itemExistente.isPresent()) {
                ItemCarrito item = itemExistente.get();
                Integer cantidadTotal = item.getCantidad() + cantidad;

                if (producto.getStock() == null || producto.getStock() < cantidadTotal) {
                    throw new StockInsuficienteException();
                }

                item.setCantidad(cantidadTotal);
                itemCarritoRepository.save(item);
            } else {
                if (producto.getStock() == null || producto.getStock() < cantidad) {
                    throw new StockInsuficienteException();
                }

                ItemCarrito nuevoItem = new ItemCarrito();
                nuevoItem.setCarrito(carrito);
                nuevoItem.setCantidad(cantidad);
                nuevoItem.setProducto(producto);

                itemCarritoRepository.save(nuevoItem);
            }
        }

        else if (asientoId != null) {
            if (funcionId == null) {
                throw new SeleccionButacaInvalidaException();
            }

            Asiento asiento = asientoRepository.findById(asientoId)
                    .orElseThrow(AsientoNotFoundException::new);

            Funcion funcion = funcionRepository.findById(funcionId)
                    .orElseThrow(FuncionNotFoundException::new);

            // La butaca tiene que pertenecer a la sala donde se da la funcion.
            if (asiento.getSala() == null || funcion.getSala() == null
                    || !asiento.getSala().getId().equals(funcion.getSala().getId())) {
                throw new SeleccionButacaInvalidaException();
            }

            // El modelo soporta una sola funcion por carrito: si ya hay butacas de otra, se rechaza.
            if (carrito.getFuncion() != null
                    && !carrito.getFuncion().getId().equals(funcion.getId())) {
                throw new SeleccionButacaInvalidaException();
            }

            // No se puede agregar la misma butaca dos veces (choca contra el UNIQUE al confirmar).
            if (itemCarritoRepository.findByCarritoAndAsiento(carrito, asiento).isPresent()) {
                throw new SeleccionButacaInvalidaException();
            }

            // Sin asociar la funcion al carrito, el checkout nunca creaba la Entrada.
            carrito.setFuncion(funcion);
            carritoRepository.save(carrito);

            // Butaca es unitaria, se ignora la cantidad recibida (si no, calcularTotal se desincroniza del checkout).
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setCantidad(1);
            nuevoItem.setAsiento(asiento);

            itemCarritoRepository.save(nuevoItem);
        }

        else {
            // Ni producto ni butaca: no devolvemos 201 en silencio.
            throw new ItemCarritoInvalidoException();
        }

        // Cada vez que se agrega algo, el carrito tiene 5 minutos desde ahora
        carrito.setExpiraEn(java.time.LocalDateTime.now().plusMinutes(5));
        carrito.setVencido(false);
        carritoRepository.save(carrito);

        return carrito;
    }

    @Override
    @Transactional
    public Carrito eliminarItem(Long usuarioId, Long itemCarritoId)
            throws UsuarioNotFoundException, ItemCarritoNotFoundException, CarritoExpiradoException {
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);

        ItemCarrito item = this.buscarItem(carrito, itemCarritoId);

        boolean eraButaca = item.getAsiento() != null;

        itemCarritoRepository.delete(item);

        List<ItemCarrito> restantes = itemCarritoRepository.findAllByCarrito(carrito);

        // Si era la ultima butaca, liberamos la funcion (si no, queda pegado y agregarItem rechaza otras).
        if (eraButaca) {
            boolean quedanButacas = restantes.stream().anyMatch(i -> i.getAsiento() != null);
            if (!quedanButacas) {
                carrito.setFuncion(null);
            }
        }

        
        if (restantes.isEmpty()) {
            carrito.setExpiraEn(null);
        }

        carritoRepository.save(carrito);

        return carrito;
    }

    @Override
    @Transactional
    public Carrito modificarCantidad(Long usuarioId, Long itemCarritoId, Integer nuevaCantidad)
            throws UsuarioNotFoundException, ItemCarritoNotFoundException, StockInsuficienteException,
                   SeleccionButacaInvalidaException, CantidadInvalidaException, CarritoExpiradoException {

        if (nuevaCantidad == null || nuevaCantidad <= 0) {
            throw new CantidadInvalidaException();
        }

        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);

        ItemCarrito item = this.buscarItem(carrito, itemCarritoId);

        // Una butaca es unitaria: no tiene cantidad modificable (ver agregarItem).
        if (item.getAsiento() != null) {
            throw new SeleccionButacaInvalidaException();
        }

        if (item.getProducto() != null) {
            Producto producto = item.getProducto();
            if (producto.getStock() == null || producto.getStock() < nuevaCantidad) {
                throw new StockInsuficienteException();
            }
        }

        item.setCantidad(nuevaCantidad);
        itemCarritoRepository.save(item);

        return carrito;
    }

    @Override
    
    @Transactional
    public CarritoResponse obtenerCarritoDetallado(Long usuarioId) throws UsuarioNotFoundException {
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);
        List<ItemCarrito> items = itemCarritoRepository.findAllByCarrito(carrito);

        List<ItemCarritoResponse> itemsResponse = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito item : items) {
            String tipo;
            Long referenciaId;
            String nombre;
            BigDecimal precioUnitario;

            if (item.getProducto() != null) {
                Producto producto = item.getProducto();
                tipo = "PRODUCTO";
                referenciaId = producto.getId();
                nombre = producto.getNombre();
                // Mismo calculo que el checkout, para mostrar el precio con descuento ya aplicado.
                precioUnitario = PrecioUtils.precioConDescuento(producto.getPrecio(), producto.getDescuento());

            } else if (item.getAsiento() != null) {
                Asiento asiento = item.getAsiento();
                tipo = "BUTACA";
                referenciaId = asiento.getId();
                nombre = "Fila " + asiento.getFila() + " - Butaca " + asiento.getNumero();
                precioUnitario = carrito.getFuncion() != null
                        ? carrito.getFuncion().getPrecio_base()
                        : BigDecimal.ZERO;

            } else {
                // No deberia existir (agregarItem lo rechaza), pero por las dudas no lo mostramos.
                continue;
            }

            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            itemsResponse.add(new ItemCarritoResponse(
                    item.getId(),
                    tipo,
                    referenciaId,
                    nombre,
                    item.getCantidad(),
                    precioUnitario.setScale(2, RoundingMode.HALF_UP),
                    subtotal.setScale(2, RoundingMode.HALF_UP)));
        }

        Long funcionId = carrito.getFuncion() != null ? carrito.getFuncion().getId() : null;

        return new CarritoResponse(
                carrito.getId(),
                funcionId,
                itemsResponse,
                total.setScale(2, RoundingMode.HALF_UP),
                carrito.getExpiraEn(),
                Boolean.TRUE.equals(carrito.getVencido()));
    }

    @Override

    @Transactional
    public BigDecimal calcularTotal(Long usuarioId) throws UsuarioNotFoundException {

        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);
        List<ItemCarrito> items = itemCarritoRepository.findAllByCarrito(carrito);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito item : items) {
            BigDecimal precioItem = BigDecimal.ZERO;

            if (item.getProducto() != null) {
                precioItem = PrecioUtils.precioConDescuento(item.getProducto().getPrecio(), item.getProducto().getDescuento());
            } else if (item.getAsiento() != null) {
                if (carrito.getFuncion() != null) {
                    precioItem = carrito.getFuncion().getPrecio_base();
                }
            }

            BigDecimal subtotal = precioItem.multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void vaciarCarrito(Long usuarioId) throws UsuarioNotFoundException {
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);
        this.limpiar(carrito, false);
    }

    @Override
    @Transactional
    public void vaciarSiVencido(Long carritoId, java.time.LocalDateTime limite) {
        Optional<Carrito> encontrado = carritoRepository.findForUpdateById(carritoId);
        if (encontrado.isEmpty()) {
            return;
        }
        Carrito carrito = encontrado.get();

 
        if (carrito.getExpiraEn() == null || !carrito.getExpiraEn().isBefore(limite)) {
            return;
        }

        this.limpiar(carrito, true);
    }

   
    // Si el item no esta y el carrito se vacio por vencimiento, avisamos eso en vez de "item no encontrado".
    private ItemCarrito buscarItem(Carrito carrito, Long itemCarritoId)
            throws ItemCarritoNotFoundException, CarritoExpiradoException {
        Optional<ItemCarrito> item = itemCarritoRepository.findByIdAndCarrito(itemCarritoId, carrito);
        if (item.isPresent()) {
            return item.get();
        }
        if (Boolean.TRUE.equals(carrito.getVencido())) {
            throw new CarritoExpiradoException();
        }
        throw new ItemCarritoNotFoundException();
    }

    private void limpiar(Carrito carrito, boolean porVencimiento) {
        itemCarritoRepository.deleteAllByCarrito(carrito);

        // Se libera la funcion tambien, si no queda pegado a la primera para siempre.
        carrito.setFuncion(null);
        carrito.setExpiraEn(null);
        carrito.setVencido(porVencimiento);
        carritoRepository.save(carrito);
    }
}