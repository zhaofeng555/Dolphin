package com.haojg.id.node;

import java.sql.*;
import java.util.ResourceBundle;


public class WorkNodeDao {

    private Connection conn =null;
    private PreparedStatement ps=null;
    private ResultSet rs=null;

    public Long getWorkNodeId(String hostnameIpPort){
        return insertWorkNode(hostnameIpPort);
    }

    private Long insertWorkNode(String hostnameIpPort) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("jdbc");
            String driverName = resourceBundle.getString("jdbc.driverClassName");
            String jdbc = resourceBundle.getString("jdbc.url");
            String user = resourceBundle.getString("jdbc.username");
            String password = resourceBundle.getString("jdbc.password");

            Class.forName(driverName);
            conn = DriverManager.getConnection(jdbc, user, password);

            //Statement.RETURN_GENERATED_KEYS,为必传参数
            String insertSql = "insert into work_node(id, hostname_ip_port, create_datetime) values (0,"+hostnameIpPort+",now())";
            ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.executeUpdate();
            rs= ps.getGeneratedKeys();
            rs.next();
            return rs.getLong(1);

        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }finally {
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }

            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }

            if(conn !=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
