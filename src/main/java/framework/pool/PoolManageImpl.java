package framework.pool;

import framework.annotation.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@Component
public class PoolManageImpl {
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final Integer INIT_CONN = 3;
    private static final Integer MAX_CONN = 5;

    private static final Vector<MyConnection> CONNECTIONS = new Vector<>();

    static {
        System.out.println("开始初始化数据库连接池");
        try {
            Class.forName(DRIVER_CLASS);
            createConn(INIT_CONN);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    /**
     * 获取连接池中可用连接
     *
     * @return
     */
    private MyConnection getConn() {
        if (CONNECTIONS.isEmpty()) {
            System.out.println("连接池中没有连接");
            throw new RuntimeException("连接池中没有连接");
        }
        return getActiveConn();
    }

    /**
     * 同步方法来获取连接池中可用连接，在多线程情况下，只有一个线程访问该方法来获取连接，防止由于多线程情况下多个线程获取同一个连接从而引起出错
     *
     * @return
     */
    private synchronized MyConnection getActiveConn() {
        //通过循环来获取可用连接，若获取不到可用连接，则依靠无限循环来继续获取
        while (true) {
            for (MyConnection conn : CONNECTIONS) {
                if (!conn.getIsUse()) {
                    conn.setIsUse(true);
                    return conn;
                }
            }
            //根据连接池中连接数量从而判断是否增加对应的数量的连接
            if (CONNECTIONS.size() < MAX_CONN) {
                createConn(1);
            }
        }
    }

    private static void createConn(Integer count) {
        for (int i = 0; i < count; i++) {
            if (MAX_CONN > 0 && CONNECTIONS.size() >= MAX_CONN) {
                System.out.println("连接池中连接数量已经达到最大值");
                throw new RuntimeException("连接池中连接数量已经达到最大值");
            }
            try {
                Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                CONNECTIONS.add(new MyConnection(conn, false));
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * 保存对象
     *
     * @param t
     * @param <T>
     * @return
     */
    public <T> Integer save(T t) {
        MyConnection conn = getConn();
        Map<String, String> fieldValueMap = new HashMap<>();
        Class<?> tClass = t.getClass();
        Field[] tFields = tClass.getDeclaredFields();
        for (Field field : tFields) {
            Object value = getFieldValueByName(field.getName(), t);
            if (null != value) {
                fieldValueMap.put("`" + field.getName() + "`", "'" + value + "'");
            }
        }
        //生成sql
        String sql = new StringBuffer("insert into ")
                .append("`").append(tClass.getSimpleName().toLowerCase()).append("` ")
                .append("(").append(String.join(", ", fieldValueMap.keySet())).append(") ")
                .append("values (").append(String.join(", ", fieldValueMap.values())).append(");")
                .toString();
        try {
            //返回生成的key
            PreparedStatement statement = conn.getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            statement.close();
            conn.close();
            return id;
        } catch (SQLException e) {
            System.out.println(e);
            return -1;
        }
    }

    /**
     * 查询
     *
     * @param id
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T get(Integer id, Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            MyConnection conn = getConn();
            String sql = "select from `" + tClass.getSimpleName().toLowerCase() + "` where id=?;";
            PreparedStatement statement = conn.getConn().prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i + 1);
                Object value = resultSet.getObject(columnName);
                setFieldValueByName(columnName, value, t);
            }
            return t;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * 根据属性名获取属性值
     *
     * @param fieldName
     * @param o
     * @return
     */
    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private <T> void setFieldValueByName(String fieldName, Object value, T t) {
        try {
            Field field = t.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(t, value);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
