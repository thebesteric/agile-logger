package agile.logger.example.web;

import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * TestController
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-16 17:15:09
 */
@RestController
@RequestMapping("/test")
@AgileLogger(tag = "[API]")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping
    public R index() {
        return R.success();
    }

    @GetMapping("/test")
    public R test(String name, int age) {
        int result = testService.add(1, 2);
        return R.success().put("name", name).put("age", age).put("result", result);
    }

    @PostMapping("/query")
    public R query(@RequestBody User user, String name, int age) {
        R result = test(name, age);
        return R.success().put("result", result.getData()).put("user", user);
    }
}
