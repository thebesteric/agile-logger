package agile.logger.example.web.quickstart;

import agile.logger.example.web.quickstart.mock.LoginMockAdapter;
import agile.logger.example.web.quickstart.mock.MultiMethodMockAdapter;
import agile.logger.example.web.quickstart.version.LoginVersion;
import agile.logger.example.web.quickstart.version.MapVersion;
import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.annotation.Versioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    // @Autowired
    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @Value("${domain.oms-url:default}")
    private String omsUrl;

    @GetMapping
    public R sayHello(@RequestParam String name) {
        String wording = testService.sayHello(name);
        return R.success().setData(wording);
    }

    @GetMapping("/hi")
    public R sayHi(@RequestParam String name) {
        String wording = testService.sayHi(name);
        return R.success().setData(wording);
    }

    @GetMapping("/params")
    public R params(String name, int age) {
        String wording = testService.param(name, age);
        return R.success().setData(wording);
    }

    @PostMapping("/login")
    @Versioner(type = LoginVersion.class)
    public UserInfo login(@RequestBody Identity identity) {
        UserInfo userInfo = testService.login(identity);
        return userInfo;
    }

    @PostMapping("/map")
    @Versioner(type = MapVersion.class)
    public R map(@RequestBody Map<String, Object> map) {
        return R.success(map);
    }

    // @Mocker("{username: lisi, password: 1234, greeting: hello}")
    // @Mocker(target = "classpath:/mock/userInfo.json")
    // @Mocker(target = "file:/Users/keisun/demo/userInfo.json")
    // @Mocker(target = "https://yapi.shuinfo.tech/mock/398/breast-coach-api/userInfo")
    @Mocker(type = LoginMockAdapter.class)
    @PostMapping("/mock")
    public UserInfo mock(@RequestBody Identity identity) {
        return testService.login(identity);
    }

    @Mocker(type = MultiMethodMockAdapter.class)
    @PostMapping("/mock1")
    public UserInfo mock1(@RequestBody Identity identity) {
        return testService.login(identity);
    }

    @Mocker(type = MultiMethodMockAdapter.class)
    @PostMapping("/mock2")
    public R mock2(@RequestBody Identity identity) {
        return R.success(testService.login(identity));
    }
}
