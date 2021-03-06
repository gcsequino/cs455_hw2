package scaling.server;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import scaling.utils.DataUnit;
import scaling.utils.Hash;
import scaling.utils.ReadWriteUtils;
import scaling.utils.WorkUnit;

public class WorkerThread extends Thread {
    private LinkedBlockingQueue<WorkUnit> readyQueue;
    ConcurrentHashMap<String, AtomicInteger> sharedMsgsProcessed;

    public WorkerThread(LinkedBlockingQueue<WorkUnit> readyQueue, ConcurrentHashMap<String, AtomicInteger> sharedMsgsProcessed) {
        this.readyQueue = readyQueue;
        this.sharedMsgsProcessed = sharedMsgsProcessed;
    }

    @Override
    public void run() {
        while(true) {
            if(!readyQueue.isEmpty()) {
                try {
                    work(readyQueue.take());
                } catch (InterruptedException e) {  
                    e.printStackTrace();
                }
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
            sharedMsgsProcessed.get(dataUnit.client_info.toString()).getAndIncrement();
        }
    }
}
