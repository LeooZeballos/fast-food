package net.leozeballos.FastFood;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@Tag(name = "General", description = "General application endpoints")
public class AppController {

    @GetMapping("/api/v1/test")
    @Operation(summary = "Test API connectivity", description = "Returns the current status and version of the API")
    public Map<String, String> test() {
        return Map.of("status", "ok", "version", "v1");
    }
}
