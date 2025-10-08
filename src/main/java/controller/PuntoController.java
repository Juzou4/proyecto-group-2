package controller;
import dto.PuntoDTO;
import service.AlpescabService;
import org.springframework.web.bind.annotation.*;

// RF7
@RestController 
@RequestMapping("/puntos")
public class PuntoController {
    private final AlpescabService svc;
    public PuntoController(AlpescabService svc) {
         this.svc = svc; 
        }
    
    @PostMapping
    public Long crear(@RequestBody PuntoDTO dto) {
         return svc.crearPunto(dto); 
        }
}
