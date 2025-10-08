package controller;
import dto.RevisionDTO;
import service.AlpescabService;
import org.springframework.web.bind.annotation.*;
//RF10, RF11

@RestController 
@RequestMapping("/revisiones")
public class RevisionController {
    private final AlpescabService svc;
    public RevisionController(AlpescabService svc) {
         this.svc = svc; 
        }


    @PostMapping
    public Long crear(@RequestBody RevisionDTO dto) {
         return svc.crearRevision(dto); 
        }
}
