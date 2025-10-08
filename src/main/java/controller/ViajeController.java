package controller;
import org.springframework.web.bind.annotation.*;
import service.AlpescabService;

// RF9
@RestController 
@RequestMapping("/viajes")
public class ViajeController {
    private final AlpescabService svc;
    public ViajeController(AlpescabService svc) {
         this.svc = svc; 
        }

    @PostMapping("/{id}/finalizar")
    public int finalizar(@PathVariable Long id) {
         return svc.finalizarViaje(id); 
        }
}
