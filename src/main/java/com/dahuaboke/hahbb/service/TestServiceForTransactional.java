package com.dahuaboke.hahbb.service;

import com.dahuaboke.hahbb.core.aop.DataSourceGroup;
import com.dahuaboke.hahbb.core.aop.DataSourceName;
import com.dahuaboke.hahbb.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class TestServiceForTransactional {

    @Autowired
    private TestDao testDao;

    public int test7() {
        return testDao.test7();
    }

    public int test8() {
        return testDao.test8();
    }

    @DataSourceGroup("sit")
    @DataSourceName("sit-1")
    @Transactional
    public String transactional() {
        Map result = new HashMap();
        result.put("test7", test7());
//        int a = 1 / 0;
        result.put("test8", test8());
        return result.toString();
    }
}
