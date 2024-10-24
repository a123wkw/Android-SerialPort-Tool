package com.licheedev.serialtool;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 仪器测试类，用于在Android设备上执行。
 * 该测试类用于验证应用程序的上下文是否正确。
 *
 * @see <a href="http://d.android.com/tools/testing">Android 测试文档</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /**
     * 测试方法，用于验证应用程序的包名是否正确。
     * 通过获取目标应用的上下文并与预期的包名进行比较。
     *
     * @throws Exception 如果测试过程中发生任何异常
     */
    @Test
    public void testApplicationContext() throws Exception {
        // 获取被测试应用的上下文
        Context targetContext = InstrumentationRegistry.getTargetContext();

        // 验证应用的包名是否符合预期
        String expectedPackageName = "com.itdlc.coin";
        String actualPackageName = targetContext.getPackageName();
        assertEquals("应用的包名不符合预期", expectedPackageName, actualPackageName);
    }
}
