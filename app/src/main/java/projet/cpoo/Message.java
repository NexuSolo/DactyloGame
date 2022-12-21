package projet.cpoo;

import com.google.gson.internal.LinkedTreeMap;

public class Message {
    Transmission transmition;
    LinkedTreeMap<String,Object> message = new LinkedTreeMap<String,Object>();

    public Message(Transmission t, LinkedTreeMap<String,Object> l) {
        this.transmition = t;
        this.message = l;
    }

    public Transmission getTransmition() {
        return transmition;
    }

    public Object getMessage() {
        return message;
    }
    
}