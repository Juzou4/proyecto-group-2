package service;

import org.springframework.stereotype.Service;

import dao.AlpescabDAO;

import dto.CiudadDTO;
import dto.VehiculoDTO;
import dto.DisponibilidadDTO;
import dto.PuntoDTO;
import dto.RevisionDTO;
import dto.SolicitudServicioDTO;
import dto.UsuarioConductorDTO;
import dto.UsuarioServicioDTO;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;


@Service
public class AlpescabService {
    private final AlpescabDAO dao;
    public AlpescabService(AlpescabDAO dao) { this.dao = dao; }


    // RF1
    public Long crearCiudad(CiudadDTO c) { return dao.crearCiudad(c); }
    // RF2
    public Long crearUsuarioServicio(UsuarioServicioDTO u) { return dao.crearUsuarioServicio(u); }
    // RF3
    public Long crearUsuarioConductor(UsuarioConductorDTO u) { return dao.crearUsuarioConductor(u); }
    // RF4
    public Long crearVehiculo(VehiculoDTO v) { return dao.crearVehiculo(v); }
    // RF5
    public Long crearDisponibilidad(DisponibilidadDTO d) { return dao.crearDisponibilidad(d); }
    // RF6
    public int actualizarDisponibilidad(Long id, DisponibilidadDTO d) { return dao.actualizarDisponibilidad(id, d); }
    // RF7
    public Long crearPunto(PuntoDTO p) { return dao.crearPunto(p); }
    // RF8
    public Long solicitarServicio(SolicitudServicioDTO s) { return dao.solicitarServicio(s); }
    // RF9
    public int finalizarViaje(Long id) { return dao.finalizarViaje(id); }
    // RF10/11 (reusamos tabla REVISION)
    public Long crearRevision(RevisionDTO r) { return dao.crearRevision(r); }


    // RFCs
    public List<Map<String,Object>> rfc1(Long usuarioServicioId) { return dao.rfc1Historico(usuarioServicioId); }
    public List<Map<String,Object>> rfc2() { return dao.rfc2TopConductores(); }
    public List<Map<String,Object>> rfc3(Long conductorId) { return dao.rfc3IngresosConductorPorVehiculo(conductorId); }
    public List<Map<String,Object>> rfc4(Long ciudadId, OffsetDateTime desde, OffsetDateTime hasta) { return dao.rfc4UsoServiciosCiudad(ciudadId, desde, hasta); }
    }
