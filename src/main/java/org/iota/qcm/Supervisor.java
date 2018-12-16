package org.iota.qcm;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Supervisor {
    enum SupervisorStatus {
        SUPERVISOR_STATUS_INITIALIZED,
        SUPERVISOR_STATUS_RUNNING,
        SUPERVISOR_STATUS_PAUSED,
        SUPERVISOR_STATUS_STOPPED,
    }
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    Queue<Map<Environment, String>> effectQueue;
    SupervisorStatus status;

    public Supervisor() {
        effectQueue = new LinkedList<>();
    }

    public boolean start() {
        switch (status) {
            case SUPERVISOR_STATUS_PAUSED:
            case SUPERVISOR_STATUS_INITIALIZED:
                executor.submit(this::spawnTimeQuants);
                return true;
            case SUPERVISOR_STATUS_RUNNING:
                return false;
            case SUPERVISOR_STATUS_STOPPED:
                throw new NotImplementedException();
            default: break;
        }
        return false;
    }

    private Runnable spawnTimeQuants() {
        return () -> nextQuant();
    }

    private void nextQuant() {
        switch (status) {
            case SUPERVISOR_STATUS_RUNNING:
                nextWave();
                break;
            default:
                break;

        }
    }

    private void nextWave() {
        switch (status) {
            case SUPERVISOR_STATUS_RUNNING:
                nextWave();
                break;
            default:
                break;

        }
    }
}
