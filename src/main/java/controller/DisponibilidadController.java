package controller;
import dto.DisponibilidadDTO;
import service.AlpescabService;
import org.springframework.web.bind.annotation.*;
//RF5, RF6

@RestController 
@RequestMapping("/disponibilidades")
public class DisponibilidadController {
    private final AlpescabService svc;
    public DisponibilidadController(AlpescabService svc) { this.svc = svc; }

    @PostMapping
    public Long crear(@RequestBody DisponibilidadDTO dto) { return svc.crearDisponibilidad(dto); }

    @PutMapping("/{id}")
    public int actualizar(@PathVariable Long id, @RequestBody DisponibilidadDTO dto) { return svc.actualizarDisponibilidad(id, dto); }

}
