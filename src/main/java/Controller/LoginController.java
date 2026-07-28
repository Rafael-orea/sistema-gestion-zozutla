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
        try {
            InputStream is = getClass().getResourceAsStream("/images/logo.jpeg");
            if (is != null) {
                Image image = new Image(is);
                logoImage.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }
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
            showAlert("Error", "Por favor, complete todos los campos.");
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
                showAlert("Error", "No se pudo cargar la ventana principal: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Usuario o contraseña incorrectos.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}