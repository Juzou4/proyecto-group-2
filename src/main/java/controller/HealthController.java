package controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthController {
private final JdbcTemplate jdbc;
public HealthController(JdbcTemplate jdbc) { this.jdbc = jdbc; }
@GetMapping("/ping")
public String ping() { return "DB OK = " + jdbc.queryForObject("SELECT 1 FROM DUAL", Integer.class); }
}
