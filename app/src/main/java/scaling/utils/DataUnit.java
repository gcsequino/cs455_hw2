package scaling.utils;

public class DataUnit {
    public final byte[] data;
    ClientInfo client_info;

    public DataUnit(byte[] data, ClientInfo client_info) {
        this.data = data;
        this.client_info = client_info;
    }
}
