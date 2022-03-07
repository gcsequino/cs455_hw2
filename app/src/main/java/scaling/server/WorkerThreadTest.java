package scaling.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import scaling.utils.RandomBytes;

public class WorkerThreadTest {
    public static void main(String[] args) {
        List<WorkerThread> threadPool = new ArrayList<>();
        BlockingQueue<WorkUnit> readyQueue = new LinkedBlockingQueue<>();

        int numThreads = 5;
        int workUnitSize = 1000;
        int numWorkUnits = 101;

        for(int i = 0; i < numThreads; i++) {
            WorkerThread worker = new WorkerThread(readyQueue);
            threadPool.add(worker);
            worker.start();
        }

        for(int i = 0; i < numWorkUnits; i++) {
            try {
                readyQueue.put(buildWorkUnit(workUnitSize));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Finished adding %d units.\n", numWorkUnits);
    }

    private static WorkUnit buildWorkUnit(int size) {
        List<DataUnit> work = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            byte[] data = RandomBytes.randBytes();
            work.add(new DataUnit(data));
        }
        return new WorkUnit(work);
    }
}