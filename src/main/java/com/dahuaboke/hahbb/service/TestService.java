package com.dahuaboke.hahbb.service;

import com.dahuaboke.hahbb.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestDao testDao;

    public String test() {
        List<String> test = testDao.test();
        return test.toString();
    }
}
