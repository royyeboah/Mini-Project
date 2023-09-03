module com.project.encryptionsoftware {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.project.encryptionsoftware to javafx.fxml;
    exports com.project.encryptionsoftware;
}