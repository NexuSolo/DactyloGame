package projet.cpoo.Controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import projet.cpoo.App;
import projet.cpoo.GameData;
import projet.cpoo.Settings;

public class StatistiquesController {

    @FXML
    AnchorPane anchor;

    @FXML
    GridPane gridPane;

    @FXML
    public void initialize() throws Exception {
        setGraph();
        anchor.setFocusTraversable(true);
        anchor.requestFocus();
    }

    private void setGraph() {

        //TODO : faire en fonction du temps
        int dataTab[] = GameData.getPrecisionList();
        String s = (Settings.isModeTemps())?"Temps en s":"Nombre de mots";

        // dataTab[1] = 100;
        final NumberAxis xAxis = new NumberAxis(0, dataTab.length, 1);
        final NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Precision en %");
        xAxis.setLabel(s);
        final AreaChart<Number, Number> ac = new AreaChart<Number, Number>(xAxis, yAxis);

        XYChart.Series serie1 = new Series<>();
        serie1.getData().add(new XYChart.Data<>(0, 100));
        for (int i = 0; i < dataTab.length; i++) {
            System.out.println("Data " + (i+1) + " = " + dataTab[i]);
            serie1.getData().add(new XYChart.Data<>(i + 1, dataTab[i]));
        }
        // serie1.setName("Precision en moyenne");
        ac.setTitle("Precision lors de la partie");
        ac.getData().addAll(serie1);
        // ac.setTranslateY(350);
        // anchor.getChildren().add(ac);

        List<Integer> dataFreq = GameData.getFreqList();
        Optional<Integer> opt = dataFreq.stream().max(Comparator.naturalOrder());
        int max = 50;
        if (opt.isPresent()) max = opt.get();
        
        final NumberAxis xAxisFreq = new NumberAxis(0, dataFreq.size(), 1);
        final NumberAxis yAxisFreq = new NumberAxis(0,max,10);
        final AreaChart<Number,Number> freq =
        new AreaChart<Number,Number>(xAxisFreq,yAxisFreq);
        yAxisFreq.setLabel("Fluidite en ?");
        xAxisFreq.setLabel(s);
        freq.setTitle("Fluidite lors de la partie");
        XYChart.Series serieFreq = new Series<>();
        for (int i = 0; i < dataFreq.size(); i++) {
            serieFreq.getData().add(new XYChart.Data<>(i, dataFreq.get(i)));
        }
        serieFreq.setName("Fluidité en moyenne");
        freq.getData().addAll(serieFreq);
        // freq.setTranslateX(700);
        // freq.setTranslateY(350);
        freq.setTitle("Title freq");
        freq.setId("null");
        gridPane.add(ac, 0,2);
        gridPane.add(freq, 1,2);
        // anchor.getChildren().add(freq);

    }
    @FXML
    public void keyPressed(KeyEvent e) throws IOException {
        System.out.println("appui");
        if (e.getCode().equals(KeyCode.ESCAPE)) {
            App.setRoot("menu");
        }    
    }
}
