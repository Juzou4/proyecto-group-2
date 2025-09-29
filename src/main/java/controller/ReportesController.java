package controller;
import service.AlpescabService;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

// RFC1–RFC4
@RestController 
@RequestMapping("/reportes")
public class ReportesController {
    private final AlpescabService svc;
    public ReportesController(AlpescabService svc) { this.svc = svc; }


    @GetMapping("/historico-usuario")
    public List<Map<String,Object>> rfc1(@PathVariable Long usuarioServicioId) {
        return svc.rfc1(usuarioServicioId);
    }

    @GetMapping("/top-conductores")
    public List<Map<String,Object>> rfc2() { return svc.rfc2(); }

    @GetMapping("/ingresos-conductor")
    public List<Map<String,Object>> rfc3(@PathVariable Long conductorId) { return svc.rfc3(conductorId); }

    @GetMapping("/uso-ciudad")
    public List<Map<String,Object>> rfc4(@PathVariable Long ciudadId,@PathVariable OffsetDateTime desde,@PathVariable OffsetDateTime hasta) {
    return svc.rfc4(ciudadId, desde, hasta);
    }
}
