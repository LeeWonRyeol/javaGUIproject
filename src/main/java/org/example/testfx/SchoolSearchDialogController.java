package org.example.testfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SchoolSearchDialogController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> resultListView;
    @FXML
    private Button selectButton;

    // 내부적으로 "학교명(주소)" -> SchoolID로 매핑
    private Map<String, Integer> schoolMap = new HashMap<>();

    // 선택된 항목(학교명(주소))를 저장
    private String selectedSchoolDisplay;

    public String getSelectedSchoolDisplay() {
        return selectedSchoolDisplay;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 검색어 입력 시마다 DB 검색 수행
        searchField.textProperty().addListener((obs, oldVal, newVal) -> performSearch());

        // ListView 아이템 더블 클릭 시 선택 처리
        resultListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                selectSchool();
            }
        });
    }

    /**
     * 입력된 검색어를 이용하여 DB에서 학교 정보를 조회한 후, ListView와 매핑 Map 업데이트
     */
    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            resultListView.getItems().clear();
            schoolMap.clear();
            return;
        }

        List<String> displayList = new ArrayList<>();
        String sql = "SELECT SchoolID, SchoolName, address FROM school WHERE SchoolName LIKE ?";
        Connection connection = Connect.connect_DB("cbt2");
        if (connection == null) {
            System.out.println("데이터베이스 연결 실패!!");
            return;
        }
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();
            schoolMap.clear();  // 기존 매핑 초기화
            while (rs.next()) {
                int id = rs.getInt("SchoolID");
                String name = rs.getString("SchoolName");
                String addr = rs.getString("address");
                // "학교명(주소)" 형태로
                String displayStr = name + "(" + addr + ")";
                displayList.add(displayStr);
                schoolMap.put(displayStr, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<String> obsList = FXCollections.observableArrayList(displayList);
        resultListView.setItems(obsList);
    }

    // 선택 버튼 클릭 또는 ListView 더블클릭 시 실행
    @FXML
    private void selectSchool() {
        selectedSchoolDisplay = resultListView.getSelectionModel().getSelectedItem();
        // 팝업 창 닫기
        Stage stage = (Stage) selectButton.getScene().getWindow();
        stage.close();
    }

    // 취소 버튼 클릭 시
    @FXML
    private void onCancel() {
        selectedSchoolDisplay = null;
        Stage stage = (Stage) selectButton.getScene().getWindow();
        stage.close();
    }
}
