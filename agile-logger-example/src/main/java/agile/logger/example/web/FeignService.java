package agile.logger.example.web;

import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "example", url = "https://yapi.shuinfo.tech")
@AgileLogger(tag = "[FEIGN]", extra = "just in feign")
public interface FeignService {

    @GetMapping("/mock/298/global/uploadImg1")
    Object get(@RequestParam String name);

}
