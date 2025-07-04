package com.dahuaboke.hahbb.controller;

import com.dahuaboke.hahbb.core.config.DynamicDataSource;
import com.dahuaboke.hahbb.service.TestService;
import com.dahuaboke.hahbb.service.TestServiceForTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private TestService testService;
    @Autowired
    private TestServiceForTransactional testServiceForTransactional;

    @RequestMapping("/select")
    public String select() {
        Map result = new HashMap() {{
            put("test1", testService.test1());
            put("test2", testService.test2());
            put("test3", testService.test3());
            put("test4", testService.test4());
        }};
        return result.toString();
    }

    @RequestMapping("/save")
    public String save() {
        Map result = new HashMap() {{
            put("test5", testService.test5());
            put("test6", testService.test6());
        }};
        return result.toString();
    }

    @RequestMapping("/transactional")
    public String transactional() {
        return testServiceForTransactional.transactional();
    }
}
