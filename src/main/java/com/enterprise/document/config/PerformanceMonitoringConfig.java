package com.enterprise.document.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;

/**
 * 性能监控配置类
 * 提供系统性能监控和JMX管理功能
 */
@Configuration
@ManagedResource(objectName = "com.enterprise.document:type=PerformanceMonitor", description = "Document Management System Performance Monitor")
public class PerformanceMonitoringConfig {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringConfig.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");

    private final MemoryMXBean memoryBean;
    private final RuntimeMXBean runtimeBean;
    private long startTime;

    public PerformanceMonitoringConfig() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.runtimeBean = ManagementFactory.getRuntimeMXBean();
    }

    @PostConstruct
    public void init() {
        this.startTime = System.currentTimeMillis();
        logger.info("Performance monitoring initialized");
        performanceLogger.info("System started - Memory: {} MB", 
            memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024);
    }

    @ManagedAttribute(description = "Current heap memory usage in MB")
    public long getHeapMemoryUsage() {
        return memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024;
    }

    @ManagedAttribute(description = "Maximum heap memory in MB")
    public long getMaxHeapMemory() {
        return memoryBean.getHeapMemoryUsage().getMax() / 1024 / 1024;
    }

    @ManagedAttribute(description = "System uptime in minutes")
    public long getUptimeMinutes() {
        return (System.currentTimeMillis() - startTime) / 1000 / 60;
    }

    @ManagedAttribute(description = "JVM uptime in minutes")
    public long getJvmUptimeMinutes() {
        return runtimeBean.getUptime() / 1000 / 60;
    }

    @ManagedOperation(description = "Log current performance metrics")
    public void logPerformanceMetrics() {
        performanceLogger.info("Performance Metrics - Heap: {} MB, Max: {} MB, Uptime: {} min", 
            getHeapMemoryUsage(), getMaxHeapMemory(), getUptimeMinutes());
    }

    @ManagedOperation(description = "Force garbage collection")
    public void forceGarbageCollection() {
        long beforeGC = getHeapMemoryUsage();
        System.gc();
        long afterGC = getHeapMemoryUsage();
        performanceLogger.info("Garbage collection - Before: {} MB, After: {} MB, Freed: {} MB", 
            beforeGC, afterGC, beforeGC - afterGC);
    }
}