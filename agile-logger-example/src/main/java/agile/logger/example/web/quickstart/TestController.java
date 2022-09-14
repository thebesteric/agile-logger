package agile.logger.example.web.quickstart;

import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-16 17:15:09
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping
    public R sayHello(@RequestParam String name) {
        String wording = testService.sayHello(name);
        return R.success().setData(wording);
    }
}
