/**
 *
 */
package io.metersphere.api.service;

import io.metersphere.api.module.JvmInfo;

/**
 * @author mr.zhao
 */
public class JvmService {

    // 日志输出
    public static JvmInfo jvmInfo() {
        // 虚拟机级内存情况查询
        long vmFree = 0;
        long vmUse = 0;
        long vmTotal = 0;
        long vmMax = 0;
        int byteToMb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        vmTotal = rt.totalMemory() / byteToMb;
        vmFree = rt.freeMemory() / byteToMb;
        vmMax = rt.maxMemory() / byteToMb;
        vmUse = vmTotal - vmFree;
        // 获得线程总数
        ThreadGroup parentThread;
        int totalThread = 0;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
                .getParent() != null; parentThread = parentThread.getParent()) {
            totalThread = parentThread.activeCount();
        }
        return new JvmInfo(vmTotal, vmFree, vmMax, vmUse, totalThread);
    }
}
