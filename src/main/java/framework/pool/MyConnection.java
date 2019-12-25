package framework.pool;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Data
@AllArgsConstructor
public class MyConnection {
    private Connection conn = null;
    private volatile Boolean isUse = false;

    /**
     * 查询
     *
     * @param sql
     * @return
     */
    public ResultSet queryBySql(String sql) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(sql);
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return resultSet;
    }

    public void close(){
        this.isUse=false;
    }
}
