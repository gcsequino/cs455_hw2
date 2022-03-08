package scaling.server;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import scaling.utils.DataUnit;
import scaling.utils.Hash;
import scaling.utils.ReadWriteUtils;
import scaling.utils.WorkUnit;

public class WorkerThread extends Thread {
    private ConcurrentLinkedQueue<WorkUnit> readyQueue;
    private int numWorkUnitsProcessed;

    public WorkerThread(ConcurrentLinkedQueue<WorkUnit> readyQueue) {
        this.readyQueue = readyQueue;
        numWorkUnitsProcessed = 0;
    }

    @Override
    public void run() {
        while(true) {
            if(!readyQueue.isEmpty()) {
                work(readyQueue.poll());
            }
        }
    }

    private void work(WorkUnit workUnit) {
        for(DataUnit dataUnit : workUnit.getWorkQueue()) {
            String hash = Hash.SHA1FromBytes(dataUnit.data);
            try {
                //System.out.printf("[server ~ %s] sending hash back to client at %s\n", Thread.currentThread().getName(), dataUnit.client_info);
                ReadWriteUtils.writeString(hash, dataUnit.client_info.socket);
            } catch (IOException e) {
                System.out.printf("[server ~ %s] error writing data to %s\n", Thread.currentThread().getName(), dataUnit.client_info);
                e.printStackTrace();
            }
        }
        numWorkUnitsProcessed++;
    }
}
