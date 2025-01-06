package io.krystof.retro_launcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmulatorStatus {
    private boolean running;
    private String currentDemo;
    private long uptime;
    private String monitorMode;
    private String state;
    private SystemStats systemStats;
    private ProcessStats process;

    // Nested class for system statistics
    public static class SystemStats {
        private double cpuUsage;
        private double memoryUsage;
        private Double temperature; // Optional, might be null

        @JsonProperty("cpuUsage")
        public double getCpuUsage() {
            return cpuUsage;
        }

        public void setCpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
        }

        @JsonProperty("memoryUsage")
        public double getMemoryUsage() {
            return memoryUsage;
        }

        public void setMemoryUsage(double memoryUsage) {
            this.memoryUsage = memoryUsage;
        }

        @JsonProperty("temperature")
        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
    }

    // Nested class for process statistics
    public static class ProcessStats {
        private long pid;
        private double cpuPercent;
        private double memoryPercent;

        @JsonProperty("pid")
        public long getPid() {
            return pid;
        }

        public void setPid(long pid) {
            this.pid = pid;
        }

        @JsonProperty("cpu_percent")
        public double getCpuPercent() {
            return cpuPercent;
        }

        public void setCpuPercent(double cpuPercent) {
            this.cpuPercent = cpuPercent;
        }

        @JsonProperty("memory_percent")
        public double getMemoryPercent() {
            return memoryPercent;
        }

        public void setMemoryPercent(double memoryPercent) {
            this.memoryPercent = memoryPercent;
        }
    }

    // Getters and setters for main class
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @JsonProperty("currentDemo")
    public String getCurrentDemo() {
        return currentDemo;
    }

    public void setCurrentDemo(String currentDemo) {
        this.currentDemo = currentDemo;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    @JsonProperty("monitorMode")
    public String getMonitorMode() {
        return monitorMode;
    }

    public void setMonitorMode(String monitorMode) {
        this.monitorMode = monitorMode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("systemStats")
    public SystemStats getSystemStats() {
        return systemStats;
    }

    public void setSystemStats(SystemStats systemStats) {
        this.systemStats = systemStats;
    }

    public ProcessStats getProcess() {
        return process;
    }

    public void setProcess(ProcessStats process) {
        this.process = process;
    }
}