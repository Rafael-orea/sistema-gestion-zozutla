package Controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import Model.User;
import Model.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label; // Importante añadir esta importación
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class LoginController {
    @FXML
    private ImageView logoImage;

    @FXML
    public void initialize() {
        // Cargar logo
        try {
            InputStream is = getClass().getResourceAsStream("/images/logo.jpeg");
            if (is != null) {
                Image image = new Image(is);
                logoImage.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }

        // Limite de 10 caracteres en usuario
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 10) {
                usernameField.setText(newVal.substring(0, 10));
            }
        });

        // Limite de 10 caracteres en contraseña
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 10) {
                passwordField.setText(newVal.substring(0, 10));
            }
        });
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorAlert("Error", "Por favor, complete todos los campos.");
            return;
        }

        User user = userDAO.validarUser(username, password);

        if (user != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/MainView.fxml"));
                Parent root = loader.load();

                MainController mainController = loader.getController();
                if (mainController != null) {
                    mainController.setCurrentUser(user);
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Sistema de Gestión - " + user.getNombreCompleto());
                stage.setMaximized(true);
                stage.show();

            } catch (IOException e) {
                showErrorAlert("Error", "No se pudo cargar la ventana principal: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // AQUÍ LLAMAMOS A LA NUEVA ALERTA DE ERROR
            showErrorAlert("Error", "Usuario o contraseña incorrectos.");
        }
    }

    // Alerta original de información
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // NUEVO MÉTODO: Alerta de error con texto en rojo
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Cambiado a ERROR para el icono
        alert.setTitle(title);
        alert.setHeaderText(null);

        // Crear un Label personalizado para poder cambiarle el color
        Label label = new Label(message);
        label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Texto rojo y en negrita
        label.setWrapText(true); // Por si el mensaje es muy largo

        // Reemplazar el contenido estándar por nuestro Label rojo
        alert.getDialogPane().setContent(label);

        alert.showAndWait();
    }
}