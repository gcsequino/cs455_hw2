package scaling.utils;

import java.nio.channels.SocketChannel;

public class ClientInfo {
    public final String host_name;
    public final Integer port;
    public final SocketChannel socket;

    public ClientInfo(String host_name, Integer port, SocketChannel socket){
        this.host_name = host_name;
        this.port = port;
        this.socket = socket; 
    }

    public String toString(){
        return this.host_name + ":" + this.port;
    }
    
}
