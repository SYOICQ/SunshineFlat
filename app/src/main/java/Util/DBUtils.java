package Util;

import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

public class DBUtils {
    private static String driver = "com.mysql.jdbc.Driver";//mysql驱动
    private static String url = "jdbc:mysql://49.234.92.110:3306/SunShineBase"+"?useUnicode=true&characterEncoding=UTF-8";//mysql数据库连接url
    private static String user = "root";
    private static String password = "19980713";


    private static Connection getConnection(){
        Connection con = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    private static void close(Connection con, ResultSet rs, PreparedStatement ps) {
        try {
            if (con != null) {
                con.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static boolean setPstmtParam(PreparedStatement pstmt, String[] coulmn, int[] type, String sql) throws Exception {
        if(coulmn != null && type != null && coulmn.length !=0 && type.length !=0   ) {
            for (int i = 0; i<type.length; i++) {
                switch (type[i]) {
                    case Types.INTEGER:
                        pstmt.setInt(i+1, Integer.parseInt(coulmn[i]));
                        break;
                    case Types.BOOLEAN:
                        pstmt.setBoolean(i+1, Boolean.parseBoolean(coulmn[i]));
                        break;
                    case Types.CHAR:
                        pstmt.setString(i+1, coulmn[i]);
                        break;
                    case Types.DOUBLE:
                        pstmt.setDouble(i+1, Double.parseDouble(coulmn[i]));
                        break;
                    case Types.FLOAT:
                        pstmt.setFloat(i+1, Float.parseFloat(coulmn[i]));
                        break;
                    case Types.BLOB:
                        File file = new File(coulmn[i]);
                        InputStream in = new FileInputStream(file);
                        //pstmt.setBinaryStream(i+1,in);
                        pstmt.setBlob(i+1,in);
                       // in.close();
                        break;
                    default:
                        break;
                }
            }
        }
        return true;
    }

    //增、删、改
    public static boolean CUD(String[] coulmn,int[] type,String sql) throws Exception {
        if(sql == null) return false;
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        setPstmtParam(pstmt,coulmn,type,sql);
        pstmt.executeUpdate();
        close(conn,null,pstmt);
        return true;
    }

    //查询
    public static ArrayList<HashMap<String, String>> query(String[] coulmn, int[] type, String sql) throws Exception {
        if(sql == null) return null;
        ResultSet set;
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        setPstmtParam(pstmt,coulmn,type,sql);
        set = pstmt.executeQuery();
        ResultSetMetaData rsmd = set.getMetaData();//取数据库的列名
        int numberOfColumns = rsmd.getColumnCount();
        ArrayList<HashMap<String, String>>list = new ArrayList<HashMap<String, String>>();
        while(set.next()){
            HashMap<String, String> rsTree = new HashMap<String, String>();
            for(int r=1;r<numberOfColumns+1;r++) {
                if("pic".equals(rsmd.getColumnName(r))){
                    //InputStream in = set.getAsciiStream(rsmd.getColumnName(r));
                    //rsTree.put(rsmd.getColumnName(r),new String(IOUtils.toByteArray(in)));
                    Blob picture = set.getBlob(rsmd.getColumnName(r));
                    InputStream in = picture.getBinaryStream();
                    rsTree.put(rsmd.getColumnName(r),StringAndBitMapTools.bitmapToString(BitmapFactory.decodeStream(in)));
                    in.close();
                    continue;
                }
                Log.d("DB:",rsmd.getColumnName(r));
                rsTree.put(rsmd.getColumnName(r),set.getObject(rsmd.getColumnName(r)).toString());
            }
            list.add(rsTree);
        }
        close(conn,set,pstmt);
        return list;
    }
}
