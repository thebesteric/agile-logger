package agile.logger.example.web;

import agile.logger.example.web.quickstart.UserInfo;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "example", url = "https://yapi.shuinfo.tech")
@AgileLogger(extra = "just in feign")
public interface FeignService {

    @GetMapping(value = "/mock/398/breast-coach-api/userInfo")
    UserInfo getUserInfo(@RequestParam String name);

}
