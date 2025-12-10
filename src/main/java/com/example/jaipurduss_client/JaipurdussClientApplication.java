package com.example.jaipurduss_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class JaipurdussClientApplication extends Application {

	public static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(JaipurdussClientApplication.class, args);
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(JaipurdussClientApplication.class.getResource("/jaipurduss_client/DbDownloads.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 320, 240);
		stage.setMaximized(true);
		stage.setTitle("Download Society");
		stage.setScene(scene);
		stage.getIcons().add(new Image(getClass().getResource("/images/logo-small.png").toExternalForm()));
		stage.show();
	}
}
