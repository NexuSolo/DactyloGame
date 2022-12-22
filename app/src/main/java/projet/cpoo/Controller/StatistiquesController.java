package projet.cpoo.Controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.GameData;
import projet.cpoo.Settings;

public class StatistiquesController {

    @FXML
    AnchorPane anchor;

    @FXML
    GridPane gridPane;

    @FXML
    Button button;

    @FXML
    Text congrats;

    @FXML
    public void initialize() throws Exception {
        button.setFocusTraversable(false);
        anchor.setFocusTraversable(true);
        anchor.requestFocus();
        setGraph();
        setText();
    }

    private void setGraph() {
        //TODO : faire en fonction du temps
        setMotGraph();
        setPrecGraph();
        setFreqGraph();
    }

    private void setText(){
        int mots = GameData.getMotComplete();
        int temps = Settings.getTEMPS_MAX()/10;
        int mpm = (60*mots)/temps;
        String s = "Bravo vous avez tape " + mots + " mots en " + temps + " secondes";
        s += "\n Cela fait donc " + mpm + " mots par minute"; 
        congrats.getStyleClass().add("congratText");
        congrats.setStyle("-fx-text-fill: #e2b714;-fx-font-size: 25;");
        congrats.setText(s);
    }

    private void setMotGraph() {
        int wordTab[] = GameData.getWordList();
        final NumberAxis xAxis = new NumberAxis(0, wordTab.length, 1);
        final NumberAxis yAxis = new NumberAxis(0, Settings.getTEMPS_MAX()/10,Settings.getTEMPS_MAX()/100);
        xAxis.setLabel("Nombre de mots");
        yAxis.setLabel("Temps en s");
        yAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);
        final AreaChart<Number, Number> motChart = new AreaChart<Number, Number>(xAxis, yAxis);

        XYChart.Series serie1 = new Series<>();
        serie1.getData().add(new XYChart.Data<>(0, 0));
        for (int i = 0; i < wordTab.length; i++) {
            System.out.println("Data " + (i+1) + " = " + wordTab[i]);
            serie1.getData().add(new XYChart.Data<>(i + 1, wordTab[i]));
        }
        motChart.setTitle("Mot par minutes");
        motChart.setLegendVisible(false);
        motChart.getData().addAll(serie1);
        gridPane.add(motChart, 1,1);
    }

    private void setPrecGraph() {
        int precisionTab[] = GameData.getPrecisionList();
        String s = (Settings.isModeTemps())?"Temps en s":"Nombre de mots";

        final NumberAxis xAxis = new NumberAxis(0, precisionTab.length, precisionTab.length/10);
        final NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel(s);
        yAxis.setLabel("Precision en %");
        yAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        final AreaChart<Number, Number> precChart = new AreaChart<Number, Number>(xAxis, yAxis);
        
        XYChart.Series serie1 = new Series<>();
        serie1.getData().add(new XYChart.Data<>(0, 100));
        for (int i = 0; i < precisionTab.length; i++) {
            System.out.println("Data " + (i+1) + " = " + precisionTab[i]);
            serie1.getData().add(new XYChart.Data<>(i + 1, precisionTab[i]));
        }
        precChart.setLegendVisible(false);
        precChart.setTitle("Precision lors de la partie");
        precChart.getData().addAll(serie1);
        gridPane.add(precChart, 0,2);

    }

    private void setFreqGraph () {
        List<Integer> freqList = GameData.getFreqList();
        Optional<Integer> opt = freqList.stream().max(Comparator.naturalOrder());
        int max = 50;
        if (opt.isPresent()) max = opt.get();
        final NumberAxis xAxis = new NumberAxis(0, freqList.size()-1, 1);
        final NumberAxis yAxis = new NumberAxis(0,max,max*0.1);
        final AreaChart<Number,Number> freqChart = new AreaChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("Numero de caractère");
        yAxis.setLabel("Fluidite en ?");
        yAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);

        freqChart.setTitle("Fluidite lors de la partie");
        XYChart.Series serieFreq = new Series<>();
        for (int i = 0; i < freqList.size(); i++) {
            serieFreq.getData().add(new XYChart.Data<>(i, freqList.get(i)));
        }
        serieFreq.setName("Fluidité en moyenne");
        freqChart.getData().addAll(serieFreq);
        freqChart.setLegendVisible(false);
        freqChart.setTitle("Fluidite lors de la partie");
        xAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);
        gridPane.add(freqChart, 1,2);
    }

    @FXML
    public void retour() throws IOException {
        GameData.resetAll();
        App.setRoot("menu");
    }
    @FXML
    public void keyPressed(KeyEvent e) throws IOException {
        System.out.println("appui");
        if (e.getCode().equals(KeyCode.ESCAPE)) {
            App.setRoot("menu");
        }    
    }
}
