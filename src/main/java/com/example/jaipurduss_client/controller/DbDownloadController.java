package com.example.jaipurduss_client.controller;

import com.example.jaipurduss_client.controls.MyInitialization;
import com.example.jaipurduss_client.task.DbBackupTask;
import com.example.jaipurduss_client.task.FetchDbTask;
import com.example.jaipurduss_client.task.GetUrlTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.jaipurduss_client.JaipurdussClientApplication.context;

public class DbDownloadController implements MyInitialization {
    @FXML
    private TextField txtSociety, txtMobile, txtBackupPath, txtClientCode;

    @FXML
    private Button btnClose, btnDownload, btnBrowse;

    @FXML
    private Label lblStatus;

    private final String[] statuses = {
            "It take time . . .",
            "Do not close the app . . .",
            "Restoring Backup . . ."
    };
    private int statusStep = 0;
    private final String[] statuses1 = {
            "Fetching Data .",
            "Fetching Data . .",
            "Fetching Data . . ."
    };
    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                String currentStatus = statuses[statusStep];
                setStatus(currentStatus, Color.BLACK);
                statusStep = (statusStep + 1) % statuses.length;
            })

    );
    Timeline timeline1 = new Timeline(
            new KeyFrame(Duration.seconds(3), event -> {
                String currentStatus = statuses1[statusStep];
                setStatus(currentStatus, Color.BLACK);
                statusStep = (statusStep + 1) % statuses1.length;
            })
    );
    String societyCode;
    String mobileText;

    @Override
    public Node getRoot() {
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnDownload.setOnAction(e -> {
            getUrlData();
        });

        btnBrowse.setOnAction(e -> {
            restoringProcess();
        });

        btnClose.setOnAction(e -> {
            closeApplication();
        });
    }

    public void closeApplication() {
        Platform.exit();
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }

    public void setStatus(String text, Color color) {
        try {
            lblStatus.setText(text);
            lblStatus.setTextFill(color);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getUrlData() {
        try {
            String clientCodeText = txtClientCode.getText();
            societyCode = txtSociety.getText();
            mobileText = txtMobile.getText();

            if (clientCodeText == null || clientCodeText.isBlank() ||
                    societyCode == null || societyCode.isEmpty() ||
                    mobileText == null || mobileText.isEmpty()) {
                setStatus("Please enter data !", Color.RED);
                return;
            }

            var task = new GetUrlTask(clientCodeText);

            task.setOnSucceeded(e -> {
                try {
                    String url = task.get();
                    if (url == null || url.isEmpty()) {
                        System.out.println("NO URL FOUND");
                        setStatus("No Society Found!", Color.RED);
                    } else {
                        System.out.println("STARTING DB DOWNLOAD");
                        downloadSql(url);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            task.setOnFailed(f -> {
                Platform.runLater(() -> {
                    setStatus("Error Occured!", Color.RED);
                });
            });
            new Thread(task).start();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadSql(String url) {
        try {

            timeline1.setCycleCount(Timeline.INDEFINITE);
            timeline1.play();

            var task = new FetchDbTask(societyCode, mobileText, url);
            task.setOnSucceeded(e -> {
                try {
                    timeline1.stop();
                    byte[] zipContent = task.get();
                    if (zipContent == null) {
                        System.out.println("No Society Found");
                        setStatus("No Society Found!", Color.RED);
                    } else {
                        System.out.println("FOUND SQL FILE WITH SOCIETY CODE : " + societyCode);
                        savingZipFile(zipContent, societyCode);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            task.setOnFailed(f -> {
                Platform.runLater(() -> {
                    System.out.println("SOMETHING WENT WRONG!!");
                    setStatus("Error Occured!", Color.RED);
                    timeline1.stop();
                });
            });
            new Thread(task).start();

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void savingZipFile(byte[] zipContent, String societyCodeEx) {
        try {
            Path newDbPath = Paths.get(System.getProperty("user.dir"), "resources", "generatedDb");
            if (!Files.exists(newDbPath)) {
                Files.createDirectories(newDbPath);
            }

            String suggestedFileName = "db_backup (" + societyCodeEx + ")" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".zip";
            Platform.runLater(() -> {
                Path finalSavePath = newDbPath.resolve(suggestedFileName);
                File saveFile = finalSavePath.toFile();
                if (saveFile != null) {
                    try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                        fos.write(zipContent);
                        String pathToSavedFile = saveFile.getAbsolutePath();
                        Boolean b = extractPasswordProtectedZip(pathToSavedFile, "EAmcs2022");
                        setStatus("Society Downloaded Successfully!", Color.GREEN);
                    } catch (Exception e) {
                        setStatus("Society Download Failed!", Color.RED);
                        System.err.println("Error: Failed to save the downloaded zip file: " + e.getMessage());
                    }
                } else {
                    Platform.runLater(() -> {
                        setStatus("Society Download Cancelled!", Color.RED);
                    });
                }
            });
        } catch (Exception e) {
            System.out.println("error : " + e);
            throw new RuntimeException(e);
        }
    }

    public Boolean extractPasswordProtectedZip(String zipFilePath, String password) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            FileHeader header = null;
            for (FileHeader headers : fileHeaders) {
                header = headers;
            }
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            Path tempDir = Files.createTempDirectory("zip_extract_tmp");
            String destination = tempDir.toAbsolutePath().toString();
            zipFile.extractFile(header, destination);
            File fileVariable = new File(destination, header.getFileName());
            callDbBackupTask(fileVariable);
            return true;
        } catch (Exception e) {
            System.out.println(": " + e);
        }
        return false;
    }

    public void restoringProcess() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Application File");
            FileChooser.ExtensionFilter sqlFilter = new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.sql");
            fileChooser.getExtensionFilters().add(sqlFilter);
            fileChooser.setSelectedExtensionFilter(sqlFilter);
            File selectedFile = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());

            if (selectedFile != null) {
                String filePath = selectedFile.getAbsolutePath();
                txtBackupPath.setText(filePath);
                callDbBackupTask(selectedFile);
            }
        } catch (Exception e) {
            System.out.println("error : " + e);
            throw new RuntimeException(e);
        }
    }

    public void callDbBackupTask(File selectedFile) {
        try {
            var task = new DbBackupTask(selectedFile);
            task.setOnSucceeded(ee -> {
                try {
                    Boolean b = task.get();
                    timeline.stop();
                    if (b) {
                        setStatus("DB restored ! ", Color.GREEN);
                    } else {
                        setStatus("Failed to load ! ", Color.RED);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            task.setOnRunning(es -> {
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            });
            new Thread(task).start();
        } catch (Exception e) {
            System.out.println("Nimit error : " + e);
            throw new RuntimeException(e);
        }
    }
}

