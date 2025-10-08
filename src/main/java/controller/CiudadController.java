package controller;

import dto.CiudadDTO;
import service.AlpescabService;
import org.springframework.web.bind.annotation.*;

//rf1
@RestController 
@RequestMapping("/ciudades")
public class CiudadController {
    private final AlpescabService svc;
    public CiudadController(AlpescabService svc) {
         this.svc = svc; 
        }

    @PostMapping
    public Long crear(@RequestBody CiudadDTO c) {
         return svc.crearCiudad(c); 
        }
}

