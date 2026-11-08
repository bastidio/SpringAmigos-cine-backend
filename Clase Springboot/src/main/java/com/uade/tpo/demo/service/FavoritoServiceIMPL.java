package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Favorito;
import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.FavoritoResponse;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.repository.FavoritoRepository;
import com.uade.tpo.demo.repository.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoritoServiceIMPL implements FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Override
    public List<FavoritoResponse> getMisFavoritos(Usuario usuario) {
        return favoritoRepository.findByUsuarioIdOrderByAgregadoEnDesc(usuario.getId()).stream()
                .map(f -> new FavoritoResponse(
                        f.getPelicula().getId(),
                        f.getPelicula().getTitulo(),
                        f.getPelicula().getPoster_url(),
                        f.getPelicula().getClasificacion(),
                        f.getAgregadoEn()))
                .toList();
    }

    @Override
    public void agregar(Usuario usuario, Long peliculaId) throws PeliculaNotFoundException {
        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(PeliculaNotFoundException::new);
        if (favoritoRepository.existsByUsuarioIdAndPeliculaId(usuario.getId(), peliculaId)) {
            return;
        }
        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setPelicula(pelicula);
        favorito.setAgregadoEn(LocalDateTime.now());
        favoritoRepository.save(favorito);
    }

    @Override
    @Transactional
    public void quitar(Usuario usuario, Long peliculaId) {
        favoritoRepository.deleteByUsuarioIdAndPeliculaId(usuario.getId(), peliculaId);
    }
}
