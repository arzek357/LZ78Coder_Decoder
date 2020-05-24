import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;;
import java.nio.file.Files;
import java.nio.file.Path;


public class FXMLMainController {
    private ObservableList<FilePacket> clientFiles = FXCollections.observableArrayList();
    private Boolean encodedStatus = false;
    private Boolean decodedStatus = false;
    private Boolean safeModeStatus = false;
    @FXML
    private CheckBox checkBoxEncoded,checkBoxDecoded,checkBoxSafeMode;
    @FXML
    private TextArea textArea1,textArea2;
    @FXML
    private Button open1Button,open2Button,deleteButton;
    @FXML
    private TableView<FilePacket> filesTable;
    @FXML
    private TableColumn<FilePacket, Path> fileNameColumn;
    @FXML
    private TableColumn<FilePacket,Long> fileLengthColumn;
    @FXML
    private void pressDeleteButton(ActionEvent event) throws IOException {
        Files.delete(filesTable.getSelectionModel().getSelectedItem().getFile().toPath());
        clientFiles.remove(filesTable.getSelectionModel().getSelectedItem());
        initListInTableView();
    }
    @FXML
    private void press1Button(ActionEvent event){
        printFileInTextArea(textArea1);
    }
    @FXML
    private void press2Button(ActionEvent event){
            printFileInTextArea(textArea2);
    }
    @FXML
    private void changeEncodedStatus(ActionEvent event){
       encodedStatus=!encodedStatus;
    }
    @FXML
    private void changeDecodedStatus(ActionEvent event){
        decodedStatus=!decodedStatus;
    }
    @FXML
    private void changeSafeModeStatus(ActionEvent event){
        safeModeStatus=!safeModeStatus;
    }
    @FXML
    private void encodeFile(ActionEvent event) {
            FilePacket selectedFile = filesTable.getSelectionModel().getSelectedItem();
            FilePacket newFilePacket = LZ78Coder.encode(selectedFile,safeModeStatus);
            if(!encodedStatus){
                try {
                    Files.delete(selectedFile.getFile().toPath());
                } catch (IOException ex){
                    System.out.println("Ошибка перезаписи в старый файл!");
                    ex.printStackTrace();
                }
            clientFiles.remove(selectedFile);
            }
            updateFileInfo(newFilePacket,"encoded");
    }
    @FXML
    private void decodeFile(ActionEvent event) {
        FilePacket selectedFile = filesTable.getSelectionModel().getSelectedItem();
        if (!selectedFile.getFullTextInBr().substring(0,5).equals("~LZ78")){
            Alert alert = new Alert(Alert.AlertType.WARNING,"Ошибка! Данный файл нельзя декодировать, так как он не содержит метки кодирования");
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }
        FilePacket newFilePacket;
        if (selectedFile.getFullTextInBr().substring(5,6).equals("~")){
            newFilePacket=LZ78Coder.decode(selectedFile,true);
        }
        else{
            newFilePacket=LZ78Coder.decode(selectedFile,false);
        }
        if(!decodedStatus){
            try {
                Files.delete(selectedFile.getFile().toPath());
            } catch (IOException ex){
                System.out.println("Ошибка перезаписи в старый файл!");
            }
            clientFiles.remove(selectedFile);
        }
        updateFileInfo(newFilePacket,"decoded");
    }
    void start() {
        checkDirectory();
        initListInTableView();
    }
    private void checkDirectory() {
        File file = new File("src/main/resources/Files");
        if (!file.exists()){
            file.mkdirs();
        }
        else {
            File[] arrFiles = file.listFiles();
            if (arrFiles!=null){
                for (File k:arrFiles){
                    FilePacket ks = new FilePacket(k);
                    if(!clientFiles.contains(ks)){
                        clientFiles.add(ks);
                    }
                }
            }
            else{
                return;
            }
        }
    }
    private void initListInTableView(){
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileLengthColumn.setCellValueFactory(new PropertyValueFactory<>("fileLength"));
        filesTable.setItems(clientFiles);
    }
    private void printFileInTextArea(TextArea textArea){
        FilePacket file = filesTable.getSelectionModel().getSelectedItem();
        if (file.getFullTextInBr().length()==0){
            file.findTextAndWriteInBr();
        }
        textArea.setText(file.getFullTextInBr().toString());
    }
    private void updateFileInfo(FilePacket fp,String copyFileName) {
        File newFile = CopyFileModule.checkFileAndBackUniName(fp.getFile(),copyFileName);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(newFile,false))){
            bw.write(fp.getFullTextInBr().toString());
        } catch (IOException e) {
            System.out.println("Ошибка обновления данных о файле!");
            e.printStackTrace();
        }
        FilePacket newFilePacket = new FilePacket(newFile);
        if (!clientFiles.contains(newFilePacket)){
            clientFiles.add(newFilePacket);
        }
        initListInTableView();
    }
}
