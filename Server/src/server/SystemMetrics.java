package server;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class SystemMetrics {

    // Lấy thông tin về bộ nhớ RAM
    public static String getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory(); // Bộ nhớ hiện tại đang sử dụng
        long freeMemory = runtime.freeMemory();   // Bộ nhớ còn lại
        long usedMemory = totalMemory - freeMemory; // Bộ nhớ đã sử dụng
        return String.format("Total Memory: %d MB\nUsed Memory: %d MB\nFree Memory: %d MB", 
                             totalMemory / (1024 * 1024), // Chuyển sang MB
                             usedMemory / (1024 * 1024), 
                             freeMemory / (1024 * 1024));
    }

    // Lấy thông tin về CPU
    public static String getCpuInfo() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        int availableProcessors = osBean.getAvailableProcessors(); // Số lõi CPU
        double systemCpuLoad = osBean.getSystemCpuLoad() * 100;  // Tỷ lệ sử dụng CPU
        return String.format("Available Processors: %d\nCPU Load: %.2f%%", availableProcessors, systemCpuLoad);
    }

    // Phương thức chuyển đổi các thông tin hệ thống thành chuỗi
    public static String ToString() {
        return getMemoryInfo() + "\n" + getCpuInfo();
    }
}
