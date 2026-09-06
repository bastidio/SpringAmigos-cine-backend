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
    public Carrito obtenerCarritoPorUsuario(Long usuarioId) throws UsuarioNotFoundException {
        Optional<Carrito> carritoExistente = carritoRepository.findByUsuarioId(usuarioId);

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

    @Override
    @Transactional
    public Carrito agregarItem(Long usuarioId, Long productoId, Long asientoId, Long funcionId, Integer cantidad)
        throws UsuarioNotFoundException, ProductoNotFoundException, AsientoNotFoundException,
                   StockInsuficienteException, FuncionNotFoundException, SeleccionButacaInvalidaException,
                   ItemCarritoInvalidoException, CantidadInvalidaException {

        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);

        // La cantidad solo aplica a productos: en la rama de butaca se fuerza a 1.
        // Sin este chequeo, un null revienta con NPE al comparar contra el stock
        // (500 crudo) y un negativo entra y deja el total del carrito en rojo.
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

            // Una butaca no se puede agregar dos veces al mismo carrito: al confirmar
            // chocaria contra el UNIQUE(funcion_id, asiento_id) y dejaria la compra
            // bloqueada hasta vaciar el carrito.
            if (itemCarritoRepository.findByCarritoAndAsiento(carrito, asiento).isPresent()) {
                throw new SeleccionButacaInvalidaException();
            }

            // ESTO es lo que faltaba: asociar la funcion al carrito.
            // Sin esta linea, el checkout nunca creaba la Entrada (el "agujero negro").
            carrito.setFuncion(funcion);
            carritoRepository.save(carrito);

            // Una butaca es unitaria: se ignora la cantidad recibida y se fuerza a 1.
            // Si no, calcularTotal (precio_base x cantidad) queda desincronizado del
            // checkout, que cobra precio_base una sola vez y crea una unica Entrada.
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setCantidad(1);
            nuevoItem.setAsiento(asiento);

            itemCarritoRepository.save(nuevoItem);
        }

        else {
            // Ni producto ni butaca: la request no pide nada concreto. No se
            // devuelve 201 en silencio (regla de oro: toda request tiene una
            // response que refleja lo que realmente paso).
            throw new ItemCarritoInvalidoException();
        }

        return carrito;
    }

    @Override
    @Transactional
    public Carrito eliminarItem(Long usuarioId, Long itemCarritoId) throws UsuarioNotFoundException, ItemCarritoNotFoundException {
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);

        ItemCarrito item = itemCarritoRepository.findByIdAndCarrito(itemCarritoId, carrito)
                .orElseThrow(ItemCarritoNotFoundException::new);

        boolean eraButaca = item.getAsiento() != null;

        itemCarritoRepository.delete(item);

        // Si era la ultima butaca del carrito, se libera la funcion. Sin esto el
        // carrito queda pegado a esa funcion aunque no le quede ninguna butaca, y
        // agregarItem rechaza las de cualquier otra (mismo caso que vaciarCarrito).
        if (eraButaca) {
            List<ItemCarrito> restantes = itemCarritoRepository.findAllByCarrito(carrito);
            boolean quedanButacas = restantes.stream().anyMatch(i -> i.getAsiento() != null);

            if (!quedanButacas) {
                carrito.setFuncion(null);
                carritoRepository.save(carrito);
            }
        }

        return carrito;
    }

    @Override
    @Transactional
    public Carrito modificarCantidad(Long usuarioId, Long itemCarritoId, Integer nuevaCantidad)
            throws UsuarioNotFoundException, ItemCarritoNotFoundException, StockInsuficienteException,
                   SeleccionButacaInvalidaException, CantidadInvalidaException {

        if (nuevaCantidad == null || nuevaCantidad <= 0) {
            throw new CantidadInvalidaException();
        }

        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);

        ItemCarrito item = itemCarritoRepository.findByIdAndCarrito(itemCarritoId, carrito)
                .orElseThrow(ItemCarritoNotFoundException::new);

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
                // Mismo calculo que usa el checkout: el precio que se muestra
                // es el que se va a cobrar, con el descuento ya aplicado.
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
                // Item sin producto ni butaca: no deberia existir (agregarItem lo
                // rechaza), pero si esta en la base no lo mostramos como valido.
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
                total.setScale(2, RoundingMode.HALF_UP));
    }

    @Override
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
        itemCarritoRepository.deleteAllByCarrito(carrito);

        // Al vaciar el carrito se libera la funcion asociada. Sin esto el carrito
        // queda pegado a la primera funcion para siempre y agregarItem rechaza
        // cualquier butaca de otra funcion.
        carrito.setFuncion(null);
        carritoRepository.save(carrito);
    }
}