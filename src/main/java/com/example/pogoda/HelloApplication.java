package com.example.pogoda;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.Scanner;

public class HelloApplication extends Application {

    private TextField cityField;
    private TextArea resultArea;
    private ImageView weatherIcon;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("API Pogody");

        cityField = new TextField();
        cityField.setPromptText("Wpisz nazwę miasta");

        weatherIcon = new ImageView();
        weatherIcon.setFitWidth(50);
        weatherIcon.setFitHeight(50);

        Button searchButton = new Button("Szukaj");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String cityName = cityField.getText();
                if (!cityName.isEmpty()) {
                    getWeatherData(cityName);
                }
            }
        });

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);

        Button closeButton = new Button("Zamknij");
        closeButton.setOnAction(e -> primaryStage.close());

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll( cityField, searchButton, weatherIcon, resultArea, closeButton);

        Scene scene = new Scene(vbox, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void getWeatherData(String cityName) {
        try {

            String apiKey = "";  // Podaj swój klucz API
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&lang=pl";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            String responseBody = response.toString();


            String weatherDescription = extractJsonValue(responseBody, "\"description\":\"", "\"");
            String iconCode = extractJsonValue(responseBody, "\"icon\":\"", "\"");
            String cityNameFromAPI = extractJsonValue(responseBody, "\"name\":\"", "\"");

            String tempString = extractJsonValue(responseBody, "\"temp\":", ",");
            double tempKelvin = Double.parseDouble(tempString);
            double tempCelsius = tempKelvin - 273.15;

            String humidityString = extractJsonValue(responseBody, "\"humidity\":", ",");
            int humidity = Integer.parseInt(humidityString);


            String visibilityString = extractJsonValue(responseBody, "\"visibility\":", ",");
            int visibility = Integer.parseInt(visibilityString) / 100;  // W procentach

            String windSpeedString = extractJsonValue(responseBody, "\"speed\":", ",");
            double windSpeed = Double.parseDouble(windSpeedString);

            String windDirection = extractJsonValue(responseBody, "\"deg\":", ",");


            Image weatherImage = new Image("http://openweathermap.org/img/wn/" + iconCode + "@2x.png");
            weatherIcon.setImage(weatherImage);

            String result = String.format("Miasto: %s\nOpis: %s\nTemperatura: %.2f °C\nWilgotność: %d%%\nWidoczność: %d%%\nWiatr: %.2f m/s, %s\n",
                    cityNameFromAPI, weatherDescription, tempCelsius, humidity, visibility, windSpeed, windDirection);

            resultArea.setText(result);

        } catch (Exception e) {
            resultArea.setText("Błąd pobierania danych.\nSprawdź nazwę miasta.");
        }
    }

    private String extractJsonValue(String json, String key, String endKey) {
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf(endKey, start);
        return json.substring(start, end);
    }
}