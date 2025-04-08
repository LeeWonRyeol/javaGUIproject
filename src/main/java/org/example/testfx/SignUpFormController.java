package org.example.testfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpFormController {
    private TextField EnterUserId;
    public void Back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Stage stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        Scene scene = new Scene(root);
        stage.setTitle("Main");
        stage.setScene(scene);
        stage.show();
    }
    public void CheckDuplicate(ActionEvent event) throws IOException{
        String enterUserId = EnterUserId.getText().trim();

        if(enterUserId.isEmpty()){
            //statusLabel.setText("사용자 아이디를 입력하세요.");
            return;
        }

        // "cbt2" 데이터베이스에 연결
        Connection connection = Connect.connect_DB("cbt2");
        if(connection == null) {
            System.out.println("데이터베이스 연결 실패!!");
            return;
        }

        // SQL 쿼리: users 테이블에서 입력된 user_id가 존재하는지 확인
        String sql = "Select * from USERS WHERE UserId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, enterUserId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    int count = rs.getInt("cnt");
                    if(count > 0) {
                        //statusLabel.setText("이미 존재하는 아이디입니다.");
                    } else {
                        //statusLabel.setText("사용 가능한 아이디입니다.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //statusLabel.setText("쿼리 실행 중 오류 발생: " + e.getMessage());
        }
    }
}
