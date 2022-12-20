package projet.cpoo;

public class Message {
    Transmission transmition;
    Object message;

    public Message(Transmission t, Object s) {
        this.transmition = t;
        this.message = s;
    }

    public Transmission getTransmition() {
        return transmition;
    }

    public Object getMessage() {
        return message;
    }
    
}