import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ResetPassword {
    public static void main(String[] args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("123456");
        System.out.println("Encoded password: " + encodedPassword);
        
        String url = "jdbc:mysql://localhost:3306/example_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "ck85726913";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "UPDATE sys_user SET password = ? WHERE user_name = 'admin'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, encodedPassword);
                int rows = pstmt.executeUpdate();
                System.out.println("Updated rows: " + rows);
            }
        }
    }
}