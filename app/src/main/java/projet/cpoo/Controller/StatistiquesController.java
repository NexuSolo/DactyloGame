package projet.cpoo.Controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
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
        int temps = GameData.getTempsFinal()/10;
        int mpm = (60*mots)/temps;
        String s = "Bravo vous avez tape " + mots + " mots en " + temps + " secondes";
        s += "\nCela fait donc " + mpm + " mots par minute"; 
        congrats.setText(s);
    }

    private void setMotGraph() {
        System.out.println("GD TF = " + GameData.getTempsFinal()*0.01);
        List<Integer> wordTab = GameData.getWordList();
        final NumberAxis yAxis;
        final NumberAxis xAxis;
        if (Settings.isModeTemps()) {
            yAxis = new NumberAxis(0, GameData.getMotComplete(), 1);
            xAxis = new NumberAxis(0, GameData.getTempsFinal()*.1,GameData.getTempsFinal()*.01);
            yAxis.setLabel("Nombre de mots");
            xAxis.setLabel("Temps en s");
        }
        else {
            yAxis = new NumberAxis(0, GameData.getMotComplete(), 1);
            xAxis = new NumberAxis(0, GameData.getTempsFinal()*.1,GameData.getTempsFinal()*.01);
            xAxis.setLabel("Nombre de mots");
            yAxis.setLabel("Temps en s");
        }
       
        
        yAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);
        final AreaChart<Number, Number> motChart = new AreaChart<Number, Number>(xAxis, yAxis);

        XYChart.Series serie1 = new Series<>();
        serie1.getData().add(new XYChart.Data<>(0, 0));
        if (Settings.isModeTemps()) {

            for (int i = 0; i < wordTab.size(); i++) {
                double x = i * (GameData.getTempsFinal()*0.01);
                System.out.println("Add x " + (x) + " y  " + wordTab.get(i));
                if (Settings.isModeTemps()) serie1.getData().add(new XYChart.Data<>(x,wordTab.get(i)));
                else serie1.getData().add(new XYChart.Data<>(x,wordTab.get(i)));
            }
        }
        else {
            for (int i = 0; i < wordTab.size(); i++) {
                double x = i * (GameData.getTempsFinal()*0.01);
                System.out.println("2Add x " + (wordTab.get(i)) + " y  " + i);
                serie1.getData().add(new XYChart.Data<>(wordTab.get(i),i));
            } 
        }
        motChart.setTitle("Mot par minutes");
        motChart.setLegendVisible(false);
        motChart.getData().addAll(serie1);
        gridPane.add(motChart, 1,1);
    }

    private void setPrecGraph() {
        List<Integer> precList = GameData.getPrecList();
        String s = (Settings.isModeTemps())?"Temps en s":"Nombre de mots";

        final NumberAxis xAxis = new NumberAxis(0, precList.size(), precList.size()/10);
        final NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel(s);
        yAxis.setLabel("Pr\u00e9cision en %");
        yAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        final AreaChart<Number, Number> precChart = new AreaChart<Number, Number>(xAxis, yAxis);
        
        XYChart.Series serie1 = new Series<>();
        serie1.getData().add(new XYChart.Data<>(0, 100));
        for (int i = 0; i < precList.size(); i++) {
            serie1.getData().add(new XYChart.Data<>(i + 1, precList.get(i)));
        }
        precChart.setLegendVisible(false);
        precChart.setTitle("Pr\u00e9cision lors de la partie");
        precChart.getData().addAll(serie1);
        gridPane.add(precChart, 0,2);

    }

    private void setFreqGraph () {
        List<Integer> freqList = GameData.getFreqList();
        Optional<Integer> opt = freqList.stream().max(Comparator.naturalOrder());
        int max = 50;
        if (opt.isPresent()) max = opt.get();
        final NumberAxis xAxis = new NumberAxis(0, freqList.size()-1, 1);
        final NumberAxis yAxis = new NumberAxis(0,max,max*0.01);
        final AreaChart<Number,Number> freqChart = new AreaChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("Numero de caract\u00e8re");
        yAxis.setLabel("Temps entre 2 caractères en 10e de secondes");
        yAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);

        freqChart.setTitle("Fluidit\u00e9 lors de la partie");
        XYChart.Series serieFreq = new Series<>();
        double pas = freqList.size() * 0.1;
        for (int i = 0; i < freqList.size(); i+= pas ) {
            double j = pas * 0.5;
            int borneinf = (i > 0)?(int)(i-j):i;
            int bornesup = (i < freqList.size()-1)?(int)(i+j):i;
            List<Integer> sub = freqList.subList(borneinf,bornesup);
            double moy = sub.stream().reduce(0,( x,y ) -> x + y)/sub.size();
            serieFreq.getData().add(new XYChart.Data<>(i, moy));
        }
        serieFreq.setName("Fluidit\u00e9 en moyenne");
        freqChart.getData().addAll(serieFreq);
        freqChart.setLegendVisible(false);
        freqChart.setTitle("Fluidit\u00e9 lors de la partie");
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
