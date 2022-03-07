package scaling.utils;

import java.nio.channels.SocketChannel;

public class ClientInfo {
    public String host_name;
    public Integer port;
    public SocketChannel socket;

    public ClientInfo(String host_name, Integer port, SocketChannel socket){
        this.host_name = host_name;
        this.port = port;
        this.socket = socket; 
    }

    public String toString(){
        return this.host_name + ":" + this.port;
    }
    
}
