package com.dahuaboke.hahbb.service;

import com.dahuaboke.hahbb.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestDao testDao;

    public String test1() {
        List<String> test = testDao.test1();
        return test.toString();
    }

    public String test2() {
        List<String> test = testDao.test2();
        return test.toString();
    }

    public String test3() {
        List<String> test = testDao.test3();
        return test.toString();
    }

    public String test4() {
        List<String> test = testDao.test4();
        return test.toString();
    }

    public int test5() {
        return testDao.test5();
    }

    public int test6() {
        return testDao.test6();
    }
}
