package controller;
import dto.SolicitudServicioDTO;
import service.AlpescabService;
import org.springframework.web.bind.annotation.*;
// RF8

@RestController 
@RequestMapping("/servicios")
public class ServiciosController {
    private final AlpescabService svc;
    public ServiciosController(AlpescabService svc) {
         this.svc = svc;
         }

    @PostMapping("/solicitar")
    public Long solicitar(@RequestBody SolicitudServicioDTO dto) {
         return svc.solicitarServicio(dto); 
        }
}
