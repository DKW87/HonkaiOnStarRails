package com.github.dkw87.honkaionstarrails.controller;


import com.github.dkw87.honkaionstarrails.shared.utility.dev.DevUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class DevController {

    @FXML
    public TextField scanHexTextField;
    @FXML
    public Button scanHexButton;
    @FXML
    public TextField scanTextTextField;
    @FXML
    public Button scanTextButton;
    @FXML
    public TextField bytesToHexTextField;
    @FXML
    public Button bytesToHexButton;

    @FXML
    public void scanHexSig() {
        DevUtil.scanForSignature(scanHexTextField.getText());
    }

    @FXML
    public void scanTextSig() {
        DevUtil.scanForText(scanTextTextField.getText());
    }

    @FXML
    public void convertBytesToHex() {
    }
}
