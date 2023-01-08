package projet.cpoo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import projet.cpoo.Model.EntrainementModel;
import java.util.concurrent.TimeUnit;

public class TestEntrainementJeu {
    @Test void testCharCorrect() {
        EntrainementModel model = new EntrainementModel();
        model.initialize();
        String mot = model.getListeMots().get(0);
        model.ajoutChar(String.valueOf(mot.charAt(0)));
        assert(model.getMotAct().equals(mot.substring(0,1)));
    }

    @Test void testMotComplete() {
        EntrainementModel model = new EntrainementModel();
        model.initialize();
        String mot = model.getListeMots().get(0);
        for (int i = 0; i < mot.length(); i++) model.ajoutChar("a");
        model.validationMot();
        assertEquals(1,model.getMotComplete());
    }

    @Test void testMotCorrect() {
        EntrainementModel model = new EntrainementModel();
        model.initialize();
        String mot = model.getListeMots().get(0);
        for (int i = 0; i < mot.length(); i++) model.ajoutChar(String.valueOf(mot.charAt(i)));
        model.validationMot();
        assertEquals(1,model.getMotCorrect());
    }

    @Test void testMotIncorrect() {
        EntrainementModel model = new EntrainementModel();
        model.initialize();
        String mot = model.getListeMots().get(0);
        for (int i = 0; i < mot.length(); i++) model.ajoutChar(String.valueOf("f"));
        model.validationMot();
        assertEquals(0,model.getMotCorrect());
    }

    @Test void testFinJeuModeMots(){
        Settings.setModeMots();
        EntrainementModel model = new EntrainementModel();
        model.initialize();
        
        for (int j = 0; j < Settings.getLIMITE_MAX();j++) {
            String mot = model.getListeMots().get(j);
            for (int i = 0; i < mot.length(); i++) model.ajoutChar(String.valueOf(mot.charAt(i)));
            model.validationMot();
        }
            assertEquals(Settings.getLIMITE_MAX(),model.getMotCorrect());
    }

    @Test void testFinJeuModeMots2(){
        Settings.setModeMots();
        EntrainementModel model = new EntrainementModel();
        model.initialize();
        for (int j = 0; j < Settings.getLIMITE_MAX();j++) {
            String mot = model.getListeMots().get(j);
            for (int i = 0; i < mot.length(); i++) model.ajoutChar(String.valueOf(mot.charAt(i)));
            model.validationMot();
        }
        assert(!model.isEnJeu());
    }

    @Test void testFinJeuModeMotsErreur(){
        Settings.setModeMots();
        EntrainementModel model = new EntrainementModel();
        model.initialize();
        for (int j = 0; j < Settings.getLIMITE_MAX();j++) {
            String mot = model.getListeMots().get(j);
            for (int i = 0; i < mot.length(); i++){
                if(j == 0) model.ajoutChar(String.valueOf("e"));
                else model.ajoutChar(String.valueOf(mot.charAt(i)));
            }
            model.validationMot();
        }
        assert(model.isEnJeu());
    }

    @Test void testFinJeuModeTemps(){
        Settings.setModeTemps();
        Settings.setLIMITE_MAX(10);
        EntrainementModel model = new EntrainementModel();
        model.initialize();
        model.timerStart();
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert(!model.isEnJeu());
    }

}
