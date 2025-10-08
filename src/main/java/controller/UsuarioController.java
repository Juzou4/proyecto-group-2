package controller;
import dto.*;
import service.AlpescabService;
import org.springframework.web.bind.annotation.*;

//RF2, RF3
@RestController 
@RequestMapping("/usuarios")
public class UsuarioController {

    
    private final AlpescabService svc;
    public UsuarioController(AlpescabService svc) {
         this.svc = svc; 
        }


    @PostMapping("/servicio")
    public Long crearServicio(@RequestBody UsuarioServicioDTO dto) {
         return svc.crearUsuarioServicio(dto); 
        }

    @PostMapping("/conductor")
    public Long crearConductor(@RequestBody UsuarioConductorDTO dto) {
         return svc.crearUsuarioConductor(dto); 
        }
}
