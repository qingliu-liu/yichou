package com.liu.test;

import com.liu.util.CrowdUtils;
import org.junit.Test;

public class StringTest {


    @Test
    public void testSendCode(){

        String appcode = "270aae69805c48cfbf95bd7b1b44b657";
        String redomCode = CrowdUtils.randomCode(4);
        String phoneNum = "18530923707";
        CrowdUtils.sendShortMessage(appcode,redomCode,phoneNum);
    }
}
