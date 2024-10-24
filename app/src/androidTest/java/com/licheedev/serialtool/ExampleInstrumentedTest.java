package com.licheedev.serialtool;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 仪器测试类，用于在Android设备上执行。
 * 该测试类用于验证应用上下文的正确性。
 *
 * @see <a href="http://d.android.com/tools/testing">Android Testing Documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /**
     * 测试应用上下文是否正确。
     * 验证当前应用的包名是否符合预期。
     *
     * @throws Exception 如果获取应用上下文失败
     */
    @Test
    public void useAppContext() throws Exception {
        // 获取被测试应用的上下文
        Context appContext = InstrumentationRegistry.getTargetContext();

        // 预期包名
        String expectedPackageName = "com.itdlc.coin";
        // 实际包名
        String actualPackageName = appContext.getPackageName();

        // 断言实际包名与预期包名一致
        assertEquals("应用的包名与预期不符", expectedPackageName, actualPackageName);
    }
}
