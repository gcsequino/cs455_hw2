package scaling.server;

import java.util.concurrent.BlockingQueue;

import scaling.utils.Hash;
import scaling.utils.WorkUnit;

public class WorkerThread extends Thread {
    private BlockingQueue<WorkUnit> readyQueue;
    private int numWorkUnitsProcessed;

    public WorkerThread(BlockingQueue<WorkUnit> readyQueue) {
        this.readyQueue = readyQueue;
        numWorkUnitsProcessed = 0;
    }

    @Override
    public void run() {
        while(true) {
            try {
                work(readyQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s processed %d work units\n", Thread.currentThread().getName(), numWorkUnitsProcessed);
        }
    }

    private void work(WorkUnit workUnit) {
        for(DataUnit data : workUnit.work) {
            Hash.SHA1FromBytes(data.data);
        }
        numWorkUnitsProcessed++;
    }
}
