package dto;
import java.time.OffsetDateTime;

public class ViajeDTO {
    public Long id; 
    public Long usuarioServicioId; 
    public Long conductorId; 
    public Long vehiculoId;
    public Long servicioId; 
    public Long origenId; 
    public Long destinoId;
    public OffsetDateTime salida; 
    public OffsetDateTime llegada; 
    public Double distanciaKm; 
    public Double precio;
    public String estado;
}