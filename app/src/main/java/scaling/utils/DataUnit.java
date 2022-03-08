package scaling.utils;

public class DataUnit {
    public final byte[] data;
    public final ClientInfo client_info;

    public DataUnit(byte[] data, ClientInfo client_info) {
        this.data = data;
        this.client_info = client_info;
    }

    public String toString(){
        return this.client_info.toString();
    }
}
