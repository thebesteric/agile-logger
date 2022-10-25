package agile.logger.example.web.quickstart;

import agile.logger.example.web.FeignService;
import agile.logger.example.web.quickstart.mock.LoginMockAdapter;
import agile.logger.example.web.quickstart.mock.MultiMethodMockAdapter;
import agile.logger.example.web.quickstart.version.LoginVersion;
import agile.logger.example.web.quickstart.version.MapVersion;
import io.github.thebesteric.framework.agile.logger.commons.utils.CharsetUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.JsonUtils;
import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.annotation.Versioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;
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

    @Autowired
    private FeignService feignService;

    @Autowired
    private RestTemplate restTemplate;

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


    @GetMapping("/restTemplate")
    public R restTemplate(@RequestParam String name, @RequestParam Integer age) {
        // 请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("age", age);
        String body = URLEncoder.encode(JsonUtils.toJson(requestBody), CharsetUtils.CHARSET_UTF_8);

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-name", name);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        String url = "https://yapi.shuinfo.tech/mock/398/breast-coach-api/userInfo?name="+name;
        UserInfo userInfo = restTemplate.exchange(url, HttpMethod.GET, request, UserInfo.class).getBody();

        if (userInfo != null) {
            String wording = testService.param(name, age);
            userInfo.setGreeting(wording);
        }

        return R.success(userInfo);
    }

    @GetMapping("/feign")
    public R feign(@RequestParam String name, @RequestParam Integer age) {
        UserInfo userInfo = feignService.getUserInfo();
        if (userInfo != null) {
            String wording = testService.param(name, age);
            userInfo.setGreeting(wording);
        }
        return R.success(userInfo);
    }
}
