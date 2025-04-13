package org.example.testfx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FindInfoController implements Initializable {
    @FXML
    private TextField FindByIdName;
    @FXML
    private TextField FindByIdEmail;
    @FXML
    private ComboBox<String> FindByIdDomain;
    @FXML
    private TextField FindByPwId;
    @FXML
    private TextField FindByPwEmail;
    @FXML
    private ComboBox<String> FindByPwDomain;

    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Stage stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        Scene scene = new Scene(root);
        stage.setTitle("Main");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 이메일 도메인 목록 설정
        FindByIdDomain.setItems(FXCollections.observableArrayList(
                "gmail.com", "naver.com", "daum.net", "hotmail.com", "yahoo.com"
        ));
        // 이메일 도메인 목록 설정
        FindByPwDomain.setItems(FXCollections.observableArrayList(
                "gmail.com", "naver.com", "daum.net", "hotmail.com", "yahoo.com"
        ));

    }
    public void findId(ActionEvent event) {
        String name = FindByIdName.getText().trim();
        String email = FindByIdEmail.getText().trim();
        String domain = FindByIdDomain.getValue();

        if(name.equals("") || email.equals("") || domain == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("메시지");
            alert.setHeaderText(null);
            alert.setContentText("빈칸을 입력해주세요.");
            alert.showAndWait();  // 추가 필요!
        }else{
            String userId = getUserIdByNameAndEmail(name,email,domain);
            if(userId != null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("메시지");
                alert.setHeaderText(null);
                alert.setContentText("검색된 아이디:" + userId);
                alert.showAndWait();  // 추가 필요!
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("메시지");
                alert.setHeaderText(null);
                alert.setContentText("일치하는 아이디가 없습니다...");
                alert.showAndWait();  // 추가 필요!
            }
        }
    }

    private String getUserIdByNameAndEmail(String name, String email, String domain) {
        Connection connection = Connect.connect_DB("cbt2");
        String sql = "select userId from users where UserName = ? and Email = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, name);
            pstmt.setString(2, email + "@" + domain);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("UserId");
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void findPw(ActionEvent event) {
        String id = FindByPwId.getText().trim();
        String email = FindByPwEmail.getText().trim();
        String domain = FindByPwDomain.getValue();

        if(id.equals("") || email.equals("") || domain == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("메시지");
            alert.setHeaderText(null);
            alert.setContentText("빈칸을 입력해주세요.");
            alert.showAndWait();
        }else{
            String pw = getUserPwByIdAndEmail (id,email,domain);
            if(pw != null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("메시지");
                alert.setHeaderText(null);
                alert.setContentText("비밀번호:" + pw);
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("메시지");
                alert.setHeaderText(null);
                alert.setContentText("일치하는 정보가 없습니다...");
                alert.showAndWait();
            }
        }
    }

    private String getUserPwByIdAndEmail(String id, String email, String domain) {
        Connection connection = Connect.connect_DB("cbt2");
        String sql = "select Password from users where UserId = ? and Email = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, id);
            pstmt.setString(2, email + "@" + domain);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String pw = rs.getString("Password");
                    return pw;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
