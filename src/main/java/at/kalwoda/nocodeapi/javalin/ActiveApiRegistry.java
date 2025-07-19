package at.kalwoda.nocodeapi.javalin;

import io.javalin.Javalin;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ActiveApiRegistry {

    private final Map<String, Javalin> runningApis = new ConcurrentHashMap<>();
    private final Map<String, Integer> portMapping = new ConcurrentHashMap<>();
    private final AtomicInteger portCounter = new AtomicInteger(9000);

    public int reserveNextPort() {
        return portCounter.getAndIncrement();
    }

    public void register(String key, Javalin app, int port) {
        runningApis.put(key, app);
        portMapping.put(key, port);
    }

    public Optional<Javalin> getApp(String key) {
        return Optional.ofNullable(runningApis.get(key));
    }

    public Optional<Integer> getPort(String key) {
        return Optional.ofNullable(portMapping.get(key));
    }

    public boolean stop(String key) {
        Javalin app = runningApis.remove(key);
        portMapping.remove(key);
        if (app != null) {
            app.stop();
            return true;
        }
        return false;
    }

    public List<String> listKeys() {
        return new ArrayList<>(runningApis.keySet());
    }

    public void stopAll() {
        for (String key : runningApis.keySet()) {
            stop(key);
        }
    }

    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public void registerDataSource(String projectKey, DataSource ds) {
        dataSources.put(projectKey, ds);
    }

    public DataSource getDataSource(String projectKey) {
        return dataSources.get(projectKey);
    }

}
