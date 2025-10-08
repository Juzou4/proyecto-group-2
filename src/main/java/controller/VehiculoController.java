package controller;
import dto.VehiculoDTO;
import service.AlpescabService;
import org.springframework.web.bind.annotation.*;
//RF4
@RestController 
@RequestMapping("/vehiculos")
public class VehiculoController {
    private final AlpescabService svc;
    public VehiculoController(AlpescabService svc) {
         this.svc = svc; 
        }

    @PostMapping
    public Long crear(@RequestBody VehiculoDTO dto) {
         return svc.crearVehiculo(dto); 
        }
    
}
