package org.example.testfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.*;

public class SignUpFormController implements Initializable {
    @FXML
    private TextField nameField;      // 이름
    @FXML
    private TextField EnterUserId;    // 아이디
    @FXML
    private TextField pwField;        // 비밀번호
    @FXML
    private TextField pwcField;       // 비밀번호 확인
    @FXML
    private TextField emailFront;     // 이메일 앞부분
    @FXML
    private ComboBox<String> emailDomain; // 이메일 도메인

    @FXML
    private ComboBox<String> firstPhoneNumber; // 전화번호 첫번째 콤보박스
    @FXML
    private TextField middlePhoneNumber;     // 중간 전화번호
    @FXML
    private TextField lastPhoneNumber;     // 마지막 전화번호
    @FXML
    private ComboBox<String> year;    // 생년월일: 년도
    @FXML
    private ComboBox<String> month;   // 생년월일: 월
    @FXML
    private ComboBox<String> day;     // 생년월일: 일
    @FXML
    private TextField schoolField;
    @FXML
    private Button submitBtn;
    // 학교 표시 문자열("학교명(주소)") -> 실제 SchoolID 매핑
    private Map<String, Integer> schoolMap = new HashMap<>();

    boolean check = false;

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
            System.out.println("id:" + enterUserId);
            showAlert("사용자 아이디를 입력하세요.");
            return;
        }

        // "cbt2" 데이터베이스에 연결
        Connection connection = Connect.connect_DB("cbt2");
        if (connection == null) {
            System.out.println("데이터베이스 연결 실패!!");
            return;
        }

        // SQL 쿼리: users 테이블에서 입력된 user_id가 존재하는지 확인
        String sql = "SELECT COUNT(*) as cnt FROM USERS WHERE UserId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, enterUserId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    int count = rs.getInt("cnt");
                    if(count > 0) {
                        showAlert("이미 존재하는 아이디입니다.");
                    } else {
                        showAlert("사용 가능한 아이디입니다.");
                        check = true;
                    }
                }else {
                    // 결과가 없는 경우에도 사용 가능하다고 처리
                    showAlert("사용 가능한 아이디입니다.");
                    check = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String messageText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(messageText);
        alert.show();
    }

    //아이디 입력란에 입력이 다시진행되면 중복체크를 다시 false로 바꾸는 메서드

    public void newInput(KeyEvent keyEvent) {
        //check = false;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstPhoneNumber.setItems(FXCollections.observableArrayList("010", "011", "016", "017", "018", "019"));

        // 이메일 도메인 목록 설정
        emailDomain.setItems(FXCollections.observableArrayList(
                "gmail.com", "naver.com", "daum.net", "hotmail.com", "yahoo.com"
        ));

        // 1. 연도(year) 콤보박스 데이터: 예를 들어, 1900년부터 현재 연도까지
        ObservableList<String> yearList = FXCollections.observableArrayList();
        int currentYear = java.time.LocalDate.now().getYear();
        // 최신년도부터 1900년까지 내림차순 정렬 (혹은 오름차순도 가능)
        for (int y = currentYear; y >= 1900; y--) {
            yearList.add(String.valueOf(y));
        }
        year.setItems(yearList);

        // 2. 월(month) 콤보박스 데이터: 1 ~ 12 (두 자리 포맷 사용)
        ObservableList<String> monthList = FXCollections.observableArrayList();
        for (int m = 1; m <= 12; m++) {
            monthList.add(String.format("%02d", m));
        }
        month.setItems(monthList);

        // 3. 초기 일(day) 콤보박스 데이터: 기본적으로 31일로 설정
        updateDays(); // 선택된 연도와 월이 없으면 기본 값(31일)으로 채움

        // 연도와 월이 선택될 때마다 day 콤보박스 업데이트
        year.setOnAction(event -> updateDays());
        month.setOnAction(event -> updateDays());
        middlePhoneNumber.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
        lastPhoneNumber.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
        EnterUserId.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if(newText.length() <= 11){
                return change;
            }
            return null; // 길이가 11 초과이면 변경 거부
        }));

        // 비밀번호: 최대 8글자 제한
        pwField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if(newText.length() <= 8){
                return change;
            }
            return null; // 길이가 8 초과이면 변경 거부
        }));
        pwcField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if(newText.length() <= 8){
                return change;
            }
            return null; // 길이가 8 초과이면 변경 거부
        }));
    }

    private void updateDays() {
        ObservableList<String> dayList = FXCollections.observableArrayList();
        int daysInMonth = 31; // 기본값

        // year와 month 모두 선택되어 있으면, 해당 연도의 해당 월의 일수를 계산합니다.
        if (year.getValue() != null && month.getValue() != null) {
            int y = Integer.parseInt(year.getValue());
            int m = Integer.parseInt(month.getValue());
            YearMonth yearMonth = YearMonth.of(y, m);
            daysInMonth = yearMonth.lengthOfMonth();
        }

        // day 콤보박스를 선택된 월의 최대일수로 채움
        for (int d = 1; d <= daysInMonth; d++) {
            dayList.add(String.format("%02d", d));
        }
        day.setItems(dayList);
    }

    public void submit(ActionEvent event) {
        String errorMessage = "";
        List<String> userData = new ArrayList<>();
        // 이름
        if (nameField.getText().trim().isEmpty()) {
            errorMessage = "이름 칸이 비어있습니다.";
        }

        // 아이디
        if (EnterUserId.getText().trim().isEmpty()) {
            errorMessage = "아이디 칸이 비어있습니다.\n";
        }

        // 비밀번호
        if (pwField.getText().trim().isEmpty()) {
            errorMessage ="비밀번호 칸이 비어있습니다.";
        }

        // 비밀번호 확인
        if (pwcField.getText().trim().isEmpty()) {
            errorMessage ="비밀번호 확인 칸이 비어있습니다.";
        }

        // 이메일 앞부분
        if (emailFront.getText().trim().isEmpty()) {
            errorMessage ="이메일 앞부분 칸이 비어있습니다.";
        }

        // 이메일 도메인
        if (emailDomain.getValue() == null || emailDomain.getValue().trim().isEmpty()) {
            errorMessage ="이메일 도메인 칸이 비어있습니다.";
        }
        // 전화번호 (첫번째 콤보박스)
        if (firstPhoneNumber.getValue() == null || firstPhoneNumber.getValue().trim().isEmpty() || middlePhoneNumber.getText().trim().isEmpty() || lastPhoneNumber.getText().trim().isEmpty()) {
            errorMessage ="전화번호 칸이 비어있습니다.";
        }
        // 생년월일: 년, 월, 일 각각 검사
        if (year.getValue() == null || year.getValue().trim().isEmpty() || month.getValue() == null || month.getValue().trim().isEmpty() || day.getValue() == null || day.getValue().trim().isEmpty()) {
            errorMessage ="생년월일 칸이 비어있습니다.";
        }
        // 학교 선택 검증 (schoolMap에 없으면 오류 메시지에 추가)
        String selectedText = schoolField.getText().trim();
        int schoolId = searchSchoolId(selectedText);
        if(schoolField.getText().trim().equals("")){
           errorMessage = "학교명을 입력해주세요.";
        }
        if (!pwField.getText().equals(pwcField.getText())){
            errorMessage = "비밀번호가 일치하지않습니다.";
        }
        if (!check){
            errorMessage = "아이디 중복확인을 해주세요.";
        }

        // 누락된 항목이 있다면 경고창 표시
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("오류");
            alert.setContentText(errorMessage);
            alert.show();
        } else {
            userData.add(EnterUserId.getText());
            userData.add(nameField.getText());
            userData.add(pwField.getText());  // 보통 암호는 pwField.getText()를 사용
            userData.add(emailFront.getText() + "@" + emailDomain.getValue());
            userData.add(firstPhoneNumber.getValue() + "-" + middlePhoneNumber.getText() + "-" + lastPhoneNumber.getText());
            userData.add(String.valueOf(schoolId));
            userData.add(year.getValue() + "/" + month.getValue() + "/" + day.getValue());
            userData.add("Student");
            // 모든 필드가 입력되었다면 가입 처리 진행
            submitSql(userData);
            // 가입 성공 메시지와 함께 Alert 창으로 알린 후 창 닫기
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("가입 완료");
            alert.setHeaderText(null);
            alert.setContentText("가입되었습니다.");
            alert.showAndWait();

            // 로그인 창 띄우기
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                Parent loginRoot = loader.load();
                Stage loginStage = new Stage();
                loginStage.setTitle("로그인");
                loginStage.setScene(new Scene(loginRoot));
                loginStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 현재 창(Stage)을 가져와서 닫음
            Stage stage = (Stage) schoolField.getScene().getWindow();
            stage.close();

        }
    }

    private int searchSchoolId(String selectedText) {
        Connection connection = Connect.connect_DB("cbt2");
        String sql = "select schoolId from school where schoolName = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, selectedText);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("SchoolID");
                    return id;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }


    private void submitSql(List<String> userData) {
        Connection connection = Connect.connect_DB("cbt2");
        if (connection == null) {
            System.out.println("데이터베이스 연결 실패!!");
            return;
        }

        String sql = "insert into USERS values(?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userData.get(0));
            pstmt.setString(2, userData.get(1));
            pstmt.setString(3, userData.get(2));
            pstmt.setString(4, userData.get(3));
            pstmt.setString(5, userData.get(4));
            pstmt.setInt(6, Integer.parseInt(userData.get(5)));
            pstmt.setString(7, userData.get(6));
            pstmt.setString(8, userData.get(7));

            int re = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void searchSchool(ActionEvent event) {
        String keyword  = schoolField.getText().trim();
        List<List<String>> searchResults = searchSchoolDB(keyword);
        if (searchResults.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("검색 결과");
            alert.setHeaderText(null);
            alert.setContentText("일치하는 학교명이 없습니다.");
            alert.showAndWait();
        } else {
            // ChoiceDialog를 사용하여 검색 결과 목록에서 선택하도록 함
            ChoiceDialog<List<String>> dialog = new ChoiceDialog<>(searchResults.get(0), searchResults);
            dialog.setTitle("학교 선택");
            dialog.setHeaderText("검색된 학교 목록");
            dialog.setContentText("학교 선택:");

            Optional<List<String>> result = dialog.showAndWait();
            result.ifPresent(selected -> {
                String schoolName = selected.get(1);
                schoolField.setText(schoolName);
            });
        }
    }

    private List<List<String>> searchSchoolDB(String keyword) {
        Connection conn = Connect.connect_DB("cbt2");
        String sql = "select * from school where schoolName like ?";
        List<List<String>> schools = new ArrayList<>();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("SchoolID");
                    String name = rs.getString("SchoolName");
                    String address = rs.getString("address");

                    List<String> data = new ArrayList<>();
                    data.add(String.valueOf(id));
                    data.add(name);
                    data.add(address);
                    schools.add(data);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return schools;
    }
}
