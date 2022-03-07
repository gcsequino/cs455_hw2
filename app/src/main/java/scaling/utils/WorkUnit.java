package scaling.utils;

import java.util.List;

public class WorkUnit {
    public final List<DataUnit> work;

    public WorkUnit(List<DataUnit> work) {
        this.work = work;
    }

    public boolean addDataUnit(DataUnit data){
        return work.add(data);
    }

    
}
