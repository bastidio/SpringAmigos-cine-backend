package com.uade.tpo.demo.service;


import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.OrdenResponse;
import com.uade.tpo.demo.exceptions.OrdenAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.OrdenNotFoundException;
import java.util.List;

public interface OrdenService {
    // usuario que se logueo. se valida que sea dueño u ADMIN, para no leer ocancelar ordenes ajenas.
    Orden getOrdenById(Long id, Usuario solicitante) throws OrdenAccesoDenegadoException, OrdenNotFoundException;
    List<Orden> getOrdenesByUsuarioId(Long usuarioId);
    List<Orden> getAllOrdenes();

    // Igual que las anteriores pero con el detalle resuelto, son las que expone el controller.
    OrdenResponse getOrdenResponseById(Long id, Usuario solicitante)
            throws OrdenAccesoDenegadoException, OrdenNotFoundException;
    List<OrdenResponse> getOrdenesResponseByUsuarioId(Long usuarioId);
    List<OrdenResponse> getAllOrdenesResponse();

    OrdenResponse cancelOrden(Long id, Usuario solicitante) throws OrdenAccesoDenegadoException, OrdenNotFoundException;

}
