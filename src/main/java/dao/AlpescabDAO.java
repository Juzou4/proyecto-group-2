package dao;

import java.sql.PreparedStatement;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


import dto.CiudadDTO;
import dto.VehiculoDTO;
import dto.DisponibilidadDTO;
import dto.PuntoDTO;
import dto.RevisionDTO;
import dto.SolicitudServicioDTO;
import dto.UsuarioConductorDTO;
import dto.UsuarioServicioDTO;
import exception.ApiException;
import oracle.security.o3logon.a;

//import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class AlpescabDAO {

	private final JdbcTemplate jdbc;

	public AlpescabDAO(JdbcTemplate jdbc) {this.jdbc = jdbc;}

    // Util
    private Long insertAndReturnId(String sql, Object... params) {
    KeyHolder kh = new GeneratedKeyHolder();
    jdbc.update(con -> {
        PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID"});
        for (int i=0; i<params.length; i++) ps.setObject(i+1, params[i]);
        return ps;
    }, kh);
    Number n = kh.getKey();
    return n == null ? null : n.longValue();
    }   

	// RF1: Registrar ciudad
    public Long crearCiudad(CiudadDTO c) {
        //Un INSERT simple; delega a insertAndReturnId para obtener el ID.
        return insertAndReturnId("INSERT INTO CIUDAD(NOMBRE) VALUES (?)", c.nombre);
    }

    // RF2:Registrar usuario de servicios
    public Long crearUsuarioServicio(UsuarioServicioDTO u) {
        //Inserta en tabla base USUARIO. Fija TIPO='SERVICIO'.
        Long idUsr = insertAndReturnId("INSERT INTO USUARIO(TIPO, NOMBRE, CORREO, TELEFONO, CEDULA) VALUES ('SERVICIO',?,?,?,?)",
        //Devuelve idUsr autogenerado.
                u.nombre, u.correo, u.telefono, u.cedula);
    //Crea la fila hija con los datos de pago, reutilizando idUsr como PK/FK.
    jdbc.update("INSERT INTO USUARIO_SERVICIO(ID, TARJETA_NUM, TARJETA_NOMBRE, TARJETA_VENCE, TARJETA_CVV) VALUES (?,?,?,?,?)",
        idUsr, u.tarjetaNum, u.tarjetaNombre, u.tarjetaVence, u.tarjetaCvv);
        //Devuelve el ID del usuario creado.
    return idUsr;
    }
    // RF3: Registrar usuario conductor
    public Long crearUsuarioConductor(UsuarioConductorDTO u) {
        //Igual al anterior, pero TIPO='CONDUCTOR'.
        Long idUsr = insertAndReturnId("INSERT INTO USUARIO(TIPO, NOMBRE, CORREO, TELEFONO, CEDULA) VALUES ('CONDUCTOR',?,?,?,?)",
            u.nombre, u.correo, u.telefono, u.cedula);
    //Inserta datos de licencia y fecha de expiración.
    jdbc.update("INSERT INTO USUARIO_CONDUCTOR(ID, LICENCIA, EXPIRA) VALUES (?,?,?)",
        idUsr, u.licencia, u.expira);
    return idUsr;
    }

    // RF4: Registrar vehiculo
    public Long crearVehiculo(VehiculoDTO v) {
        //Crea vehículo (incluye FK a conductor y ciudad de expedición de placa).
        return insertAndReturnId("INSERT INTO VEHICULO(TIPO, MARCA, MODELO, COLOR, PLACA, CIUDAD_EXP_PLACA_ID, CAPACIDAD, CONDUCTOR_ID) VALUES (?,?,?,?,?,?,?,?)",
            v.tipo, v.marca, v.modelo, v.color, v.placa, v.ciudadExpPlacaId, v.capacidad, v.conductorId);
    }

    // RF5: Registrar disponibilidad (validando solape)
    public Long crearDisponibilidad(DisponibilidadDTO d) {
        //si no hay solape (FIN <= nueva.INICIO) OR (INICIO >= nueva.FIN).
        //Negamos eso (NOT) para contar las que sí se traslapan.
        int overlapping = jdbc.queryForObject(
            "SELECT COUNT(*) FROM DISPONIBILIDAD WHERE CONDUCTOR_ID=? AND NOT (FIN<=? OR INICIO>=?)",
            //Chequea si hay traslape con otra disponibilidad del mismo conductor.
            Integer.class, d.conductorId, d.inicio, d.fin);

    //Si hay traslape, devolvemos 409 CONFLICT.
    if (overlapping > 0) throw new ApiException(HttpStatus.CONFLICT, "Disponibilidad solapada");
    //Inserta la nueva disponibilidad.
    return insertAndReturnId("INSERT INTO DISPONIBILIDAD(CONDUCTOR_ID, INICIO, FIN, SERVICIO_ID) VALUES (?,?,?,?)",
        d.conductorId, d.inicio, d.fin, d.servicioId);
    }

    // RF6: Modificar disponibilidad (validando solape con otras)
    public int actualizarDisponibilidad(Long id, DisponibilidadDTO d) {
        //Igual a RF5, pero excluyendo la propia (ID<>?).
        int overlapping = jdbc.queryForObject(
            "SELECT COUNT(*) FROM DISPONIBILIDAD WHERE CONDUCTOR_ID=? AND ID<>? AND NOT (FIN<=? OR INICIO>=?)",
            Integer.class, d.conductorId, id, d.inicio, d.fin);
    //Si pasa la validación, ejecuta el UPDATE y devuelve filas afectadas.
    if (overlapping > 0) throw new ApiException(HttpStatus.CONFLICT, "Disponibilidad solapada");
    return jdbc.update("UPDATE DISPONIBILIDAD SET INICIO=?, FIN=?, SERVICIO_ID=? WHERE ID=?",
        d.inicio, d.fin, d.servicioId, id);
    }

    // RF7: Registrar punto geográfico
    public Long crearPunto(PuntoDTO p) {
        //Guarda nombre, dirección y coordenadas (lat/lon) + ciudad.
    return insertAndReturnId("INSERT INTO PUNTO_GEOGRAFICO(NOMBRE, DIRECCION, LAT, LON, CIUDAD_ID) VALUES (?,?,?,?,?)",
        p.nombre, p.direccion, p.lat, p.lon, p.ciudadId);
    }

    // RF8: Solicitar servicio y asignar conductor/vehículo disponible
    public Long solicitarServicio(SolicitudServicioDTO s) {
        // Busca vehículos de conductores sin viaje activo (ni asignado ni en curso).
        //Devuelve lista de candidatos como mapas (columna→valor).
        List<Map<String,Object>> candidatos = jdbc.queryForList(
         "SELECT v.ID AS VEH_ID, ucon.ID AS COND_ID FROM VEHICULO v JOIN USUARIO_CONDUCTOR ucon ON ucon.ID=v.CONDUCTOR_ID " +
         "WHERE ucon.ID NOT IN (SELECT CONDUCTOR_ID FROM VIAJE WHERE ESTADO IN ('ASIGNADO','EN_CURSO'))"
        );
    //Si no hay candidatos, responde 409.
    if (candidatos.isEmpty()) throw new ApiException(HttpStatus.CONFLICT, "No hay conductores disponibles");

    //Selección ingenua: toma el primero (podrías ordenar por distancia/ciudad).
    Map<String,Object> pick = candidatos.get(0); 
    Long vehId = ((Number)pick.get("VEH_ID")).longValue();
    Long condId = ((Number)pick.get("COND_ID")).longValue();


    // Crea registro de viaje en estado ASIGNADO, FECHA_SALIDA = ahora
    return insertAndReturnId("INSERT INTO VIAJE(USUARIO_SERVICIO_ID, CONDUCTOR_ID, VEHICULO_ID, SERVICIO_ID, ORIGEN_ID, DESTINO_ID, FECHA_SALIDA, ESTADO) " +
        //SYSTIMESTAMP es Oracle.
        "VALUES (?,?,?,?,?,?,SYSTIMESTAMP,'ASIGNADO')",
        s.usuarioServicioId, condId, vehId, s.servicioId, s.origenId, s.destinoId);
    }

    // RF9: Registrar viaje finalizado (cálculo distancia y precio)
    public int finalizarViaje(Long viajeId) {
        // Carga IDs de origen, destino, servicio para ese viaje.
        Map<String,Object> row = jdbc.queryForMap(
        "SELECT v.ORIGEN_ID, v.DESTINO_ID, v.SERVICIO_ID FROM VIAJE v WHERE v.ID=?",
        viajeId);
    //Convierte valores de Map a Long y maneja nullable para servicio.
    Long origenId = ((Number)row.get("ORIGEN_ID")).longValue();
    Long destinoId = ((Number)row.get("DESTINO_ID")).longValue();
    Long servicioId = row.get("SERVICIO_ID") == null ? null : ((Number)row.get("SERVICIO_ID")).longValue();

    //Obtiene coordenadas de origen y destino.
    Map<String,Object> o = jdbc.queryForMap("SELECT LAT,LON FROM PUNTO_GEOGRAFICO WHERE ID=?", origenId);
    Map<String,Object> d = jdbc.queryForMap("SELECT LAT,LON FROM PUNTO_GEOGRAFICO WHERE ID=?", destinoId);

        //Calcula distancia en km usando la fórmula de Haversine
    double km = haversineKm(((Number)o.get("LAT")).doubleValue(), ((Number)o.get("LON")).doubleValue(),
                            ((Number)d.get("LAT")).doubleValue(), ((Number)d.get("LON")).doubleValue());
        //Determina tarifa base: si no hay servicio asociado usa 3000
    double tarifa = servicioId == null ? 3000d : jdbc.queryForObject("SELECT TARIFA_BASE FROM SERVICIO WHERE ID=?", Double.class, servicioId);
    //Precio = distancia × tarifa, redondeado a entero
    double precio = Math.round(km * tarifa);

    //Marca viaje como FINALIZADO, setea llegada ahora, guarda km y precio.
    return jdbc.update("UPDATE VIAJE SET FECHA_LLEGADA=SYSTIMESTAMP, DISTANCIA_KM=?, PRECIO=?, ESTADO='FINALIZADO' WHERE ID=?",
        km, precio, viajeId);
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
    //R = 6371.0: aproximación del radio medio terrestre en km
    double R = 6371.0; // km
    // toRadians(...): las funciones trigonométricas trabajan en radianes, no en grados.
    //dLat y dLon: diferencias angulares entre coordenadas.
    double dLat = Math.toRadians(lat2-lat1);
    double dLon = Math.toRadians(lon2-lon1);
    //haversine(θ) = sin²(θ/2). Es una medida “intermedia” de arco.
    double a = Math.sin(dLat/2)*Math.sin(dLat/2)
        + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
        * Math.sin(dLon/2)*Math.sin(dLon/2);
    //c = 2·atan2(√a, √(1−a)): convierte esa medida en ángulo central (en radianes) entre los dos puntos sobre la esfera.
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    //R*c: longitud del arco ⇒ distancia en superficie (gran círculo).
    return R*c;
    }

    // RF10: Revisión pasajero -> conductor (guardamos como revisión de VEHICULO? Mejor tabla propia, pero usaremos VIAJE.COMENTARIOS)
    // Para apegarse al enunciado, usamos REVISION como "review" del vehículo (proxy del conductor)
    public Long crearRevision(RevisionDTO r) {
        //Inserta una revisión de vehículo (como proxy del conductor).
        return insertAndReturnId("INSERT INTO REVISION(VEHICULO_ID, FECHA, TIPO, APROBADA, OBSERVACION) VALUES (?,?,?, ?,?)",
        //Convierte Boolean a 'S'/'N' para la columna APROBADA
            r.vehiculoId, r.fecha, r.tipo, (r.aprobada != null && r.aprobada) ? 'S' : 'N', r.observacion);
    }

    // RFC1
    public List<Map<String,Object>> rfc1Historico(Long usuarioServicioId) {
        //Devuelve listado de viajes con datos del servicio y del vehículo.
        //Ordena más reciente primero.
        return jdbc.queryForList(
            "SELECT v.ID, v.FECHA_SALIDA, v.FECHA_LLEGADA, v.DISTANCIA_KM, v.PRECIO, v.ESTADO, s.TIPO, s.NIVEL, ve.PLACA, ve.MARCA, ve.MODELO " +
            "FROM VIAJE v JOIN SERVICIO s ON s.ID=v.SERVICIO_ID JOIN VEHICULO ve ON ve.ID=v.VEHICULO_ID " +
            "WHERE v.USUARIO_SERVICIO_ID=? ORDER BY v.FECHA_SALIDA DESC", usuarioServicioId);
    }


    // RFC2
    public List<Map<String,Object>> rfc2TopConductores() {
        //Cuenta viajes FINALIZADOS por conductor.
        return jdbc.queryForList(
            "SELECT v.CONDUCTOR_ID, u.NOMBRE, COUNT(*) AS TOTAL FROM VIAJE v " +
            "JOIN USUARIO u ON u.ID = v.CONDUCTOR_ID " +
            "WHERE v.ESTADO='FINALIZADO' GROUP BY v.CONDUCTOR_ID, u.NOMBRE " +
            //FETCH FIRST 20 ROWS ONLY = límite 20 
            "ORDER BY TOTAL DESC FETCH FIRST 20 ROWS ONLY");
    }


    // RFC3
    public List<Map<String,Object>> rfc3IngresosConductorPorVehiculo(Long conductorId) {
        return jdbc.queryForList(
            //Suma el 60% del precio 
            "SELECT v.VEHICULO_ID, s.TIPO, s.NIVEL, SUM(NVL(v.PRECIO,0)*0.6) AS TOTAL_CONDUCTOR " +
            "FROM VIAJE v LEFT JOIN SERVICIO s ON s.ID=v.SERVICIO_ID " +
            "WHERE v.ESTADO='FINALIZADO' AND v.CONDUCTOR_ID=? " +
            "GROUP BY v.VEHICULO_ID, s.TIPO, s.NIVEL ORDER BY v.VEHICULO_ID", conductorId);
    }


    // RFC4
    public List<Map<String,Object>> rfc4UsoServiciosCiudad(Long ciudadId, OffsetDateTime desde, OffsetDateTime hasta) {
        //CTE (WITH base): cuenta viajes por TIPO/NIVEL de servicio en esa ciudad y rango de fechas.
        return jdbc.queryForList(
            "WITH base AS (SELECT s.TIPO, s.NIVEL, COUNT(*) AS CNT FROM VIAJE v JOIN SERVICIO s ON s.ID=v.SERVICIO_ID " +
            "JOIN PUNTO_GEOGRAFICO p ON p.ID=v.ORIGEN_ID WHERE v.FECHA_SALIDA>=? AND v.FECHA_SALIDA<? AND p.CIUDAD_ID=? " +
            "GROUP BY s.TIPO, s.NIVEL) SELECT b.TIPO, b.NIVEL, b.CNT, ROUND((b.CNT / SUM(b.CNT) OVER())*100,2) AS PORCENTAJE FROM base b ORDER BY b.CNT DESC",
            desde, hasta, ciudadId);
    }

}