package final_exam;



import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.util.Observer;
import java.util.Observable;

public class ClientObserver extends ObjectOutputStream implements Observer {

    public ClientObserver(OutputStream out) throws IOException {
        super(out);
    }


    @Override
    public void update(Observable o, Object arg){
        try {
            this.writeObject(arg);
            this.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
