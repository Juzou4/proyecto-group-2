package dto;

import java.time.OffsetDateTime;

public class RevisionDTO {
    public Long id; 
    public Long vehiculoId; 
    public OffsetDateTime fecha; 
    public String tipo; 
    public Boolean aprobada; 
    public String observacion;
}
