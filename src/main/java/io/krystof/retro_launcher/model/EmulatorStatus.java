package io.krystof.retro_launcher.model;

public class EmulatorStatus {
    private boolean running;
    private String currentDemo;
    private long uptime;

    // Getters and setters
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
    public String getCurrentDemo() { return currentDemo; }
    public void setCurrentDemo(String currentDemo) { this.currentDemo = currentDemo; }
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
}