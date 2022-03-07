package scaling.utils;

import java.util.ArrayList;
import java.util.List;

public class WorkUnit {
    public final List<DataUnit> work;
    private Integer batch_size;

    public WorkUnit(Integer batch_size) {
        this.work = new ArrayList<>();
        this.batch_size = batch_size;
    }

    public boolean addDataUnit(DataUnit data){
        return work.add(data);
    }

    public boolean isFull(){
        return work.size() == batch_size;
    }

    
}
