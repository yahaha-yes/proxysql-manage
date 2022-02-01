package com.ewivt.proxysql_manage.utils;

import com.alibaba.fastjson.JSONObject;
import com.github.jasync.sql.db.ResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<JSONObject> resultSets2ListJsonObj(ResultSet rs,boolean skipNullValue) throws SQLException {
        List<JSONObject> result = new ArrayList<>();
        List<String> md = rs.columnNames();
        int columnCount = rs.columnNames().size();
        for (int rowCount = 0; rowCount<rs.size();rowCount++){
            JSONObject jsonObject = new JSONObject();
            for (int i =0;i<columnCount;i++){
                if (skipNullValue && rs.get(rowCount).get(i)==null){
                    continue;
                }
                jsonObject.put(md.get(i),rs.get(rowCount).get(i));
            }
            result.add(jsonObject);
        }
        return result;
    }
}
