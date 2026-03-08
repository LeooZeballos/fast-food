package net.leozeballos.FastFood;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AppController {

    @GetMapping("/api/v1/test")
    public Map<String, String> test() {
        return Map.of("status", "ok", "version", "v1");
    }
}
