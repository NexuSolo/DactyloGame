package projet.cpoo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import projet.cpoo.Model.SoloModel;

public class TestSoloJeu {
 
    @Test void testSoloMotCorrect() {
        SoloModel model = new SoloModel();
        model.initialize();
        String mot = model.getListeMots().get(0);
        int tmpVie = model.getVies();
        for (int i = 0; i < mot.length(); i++) model.ajoutChar(String.valueOf(mot.charAt(i)));
        new Thread(() ->model.validationMot()).start();
        assertEquals(tmpVie,model.getVies());
    }

    @Test void testSoloMotIncorrect() {
        SoloModel model = new SoloModel();
        model.initialize();
        String mot = model.getListeMots().get(0);
        int tmpVie = model.getVies();
        for (int i = 0; i < mot.length(); i++) model.ajoutChar(String.valueOf(mot.charAt(i)));
        model.ajoutChar("8");
        assertEquals(tmpVie - 1,model.getVies());
    }

    @Test void testSoloMotIncorrect2() {
        SoloModel model = new SoloModel();
        model.initialize();
        String mot = model.getListeMots().get(0);
        int tmpVie = model.getVies();
        model.validationMot();
        int tmpVie2 = model.getVies();
        assertEquals(tmpVie-mot.length(),model.getVies());
    }

    @Test void testSoloMotSoin() {
        SoloModel model = new SoloModel();
        model.initialize();
        model.setFirstTry(true);
        model.setSoin(true);
        String mot = model.getListeMots().get(0);
        int tmpVie = model.getVies();
        for (int i = 0; i < mot.length(); i++) model.ajoutChar(String.valueOf(mot.charAt(i)));
        model.validationMot();
        assertEquals(tmpVie+mot.length(),model.getVies());
    }

    @Test void testSoloMotNiveau() {
        SoloModel model = new SoloModel();
        model.initialize();
        String mot = model.getListeMots().get(0);
        int tmpVie = model.getVies();
        for(int j = 0; j < 11; j++) {
            mot = model.getListeMots().get(0);
            for (int i = 0; i < mot.length(); i++)  {
                model.ajoutChar(String.valueOf(mot.charAt(i)));
            }
            model.validationMot();
        }
        assertEquals(2,model.getNiveau());
    }

    @Test void testMortSolo() {
        SoloModel model = new SoloModel();
        model.initialize();
        String mot = model.getListeMots().get(0);
        while(model.getVies() > 0) {
            model.validationMot();
        }
        assert(!model.isEnJeu());
    }
}
