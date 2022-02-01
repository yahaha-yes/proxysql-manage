package com.ewivt.proxysql_manage.models;

import lombok.Data;

@Data
public class MysqlServer {
    private int hostgroup_id;
    private String hostname;
    private int port = 3306;
    private int gtid_port = 0;
    private String status = "ONLINE";
    private long weight = 1000;
    private int compression = 0;
    private int max_connections = 1000;
    private long max_replication_lag = 0;
    private int use_ssl = 0;
    private int max_latency_ms = 0;
    private String comment = "";

    public String toInsertSqlString(){
        if (hostgroup_id==0){
            return "";
        }
        if (hostname==null||hostname.isEmpty()){
            return "";
        }
        return String.format("insert into mysql_servers (hostgroup_id,hostname,port,gtid_port,status,weight,compression,max_connections,max_replication_lag,use_ssl,max_latency_ms) values " +
                "(%d, '%s' , %d , %d, '%s' , %d,0,1000,0,0,0);",hostgroup_id,hostname,port,gtid_port,status,weight);
    }

    public String toDeleteSqlString(){
        if (hostgroup_id==0){
            return "";
        }
        if (hostname==null||hostname.isEmpty()){
            return "";
        }
        return String.format("delete from mysql_servers where hostgroup_id = %d and hostname = '%s';",hostgroup_id,hostname);
    }
}
