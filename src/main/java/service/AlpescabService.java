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
    // Dependencia al DAO (inyección por constructor, inmutable)
    private final AlpescabDAO dao;
    //// Spring inyecta el DAO al construir el Service
    public AlpescabService(AlpescabDAO dao) {
         this.dao = dao; 
        }

    // Cada método del Service delega la operación al DAO.
    // Aquí también es el lugar para validar, aplicar reglas de negocio y manejar transacciones si hiciera falta.

    // RF1
    public Long crearCiudad(CiudadDTO c) {
        // Delegación directa al DAO; retorna el ID generado
         return dao.crearCiudad(c); 
        }

    // RF2 Crear usuario de servicio (pasajero)
    public Long crearUsuarioServicio(UsuarioServicioDTO u) {
         return dao.crearUsuarioServicio(u); 
        }

    // RF3 Crear usuario conductor
    public Long crearUsuarioConductor(UsuarioConductorDTO u) {
         return dao.crearUsuarioConductor(u); 
        }

    // RF4 Crear vehículo
    public Long crearVehiculo(VehiculoDTO v) {
         return dao.crearVehiculo(v); 
        }

    // RF5 Crear disponibilidad (con validación de solape en el DAO)
    public Long crearDisponibilidad(DisponibilidadDTO d) {
         return dao.crearDisponibilidad(d); 
        }

    // RF6Actualizar disponibilidad
    public int actualizarDisponibilidad(Long id, DisponibilidadDTO d) {
         return dao.actualizarDisponibilidad(id, d); 
        }

    // RF7 Crear punto geográfico (origen/destino)
    public Long crearPunto(PuntoDTO p) {
         return dao.crearPunto(p); 
        }

    // RF8 Solicitar servicio (asignación de conductor/vehículo)
    public Long solicitarServicio(SolicitudServicioDTO s) {
         return dao.solicitarServicio(s); 
        }

    // RF9 Finalizar viaje (calcula distancia/precio y actualiza estado)
    public int finalizarViaje(Long id) {
         return dao.finalizarViaje(id); 
        }

    // RF10/11 Crear revisión (se reusa tabla REVISION como “review/inspección”)
    public Long crearRevision(RevisionDTO r) {
         return dao.crearRevision(r); 
        }


    // RFCs reportes

    // RFC1: Histórico de viajes de un usuario de servicio
    public List<Map<String,Object>> rfc1(Long usuarioServicioId) {
         return dao.rfc1Historico(usuarioServicioId); 
    }

     // RFC2: Top conductores por cantidad de viajes finalizados
    public List<Map<String,Object>> rfc2() {
         return dao.rfc2TopConductores(); 
    }

    // RFC3: Ingresos del conductor agregados por vehículo
    public List<Map<String,Object>> rfc3(Long conductorId) {
         return dao.rfc3IngresosConductorPorVehiculo(conductorId); 
    }

    // RFC4: Uso de servicios por ciudad y rango de fechas (con porcentajes)
    public List<Map<String,Object>> rfc4(Long ciudadId, OffsetDateTime desde, OffsetDateTime hasta) {
         return dao.rfc4UsoServiciosCiudad(ciudadId, desde, hasta); 
    }

    }
