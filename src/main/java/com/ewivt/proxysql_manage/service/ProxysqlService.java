package com.ewivt.proxysql_manage.service;

import com.alibaba.fastjson.JSONObject;
import com.ewivt.proxysql_manage.models.MysqlQueryRule;
import com.ewivt.proxysql_manage.models.MysqlServer;
import com.ewivt.proxysql_manage.utils.Utils;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.mysql.MySQLConnectionBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProxysqlService {

    @Value("${proxysql.url}")
    private String url;

    @Value("${proxysql.username}")
    private String username;

    @Value("${proxysql.password}")
    private String password;

    private static final String LOAD_SERVERS_TO_STORAGE="load mysql servers to runtime;save mysql server to disk;";

    private static final String LOAD_RULES_TO_STORAGE="load mysql query rules to runtime;save mysql query rules to disk;";

    private static final String GET_ALL_SERVERS_SQL="select * from mysql_servers;";

    private static final String GET_ALL_RULES_SQL="select * from mysql_query_rules;";

    private Connection connection;

    @PostConstruct
    public void initConnection(){
        try {
            connection = MySQLConnectionBuilder.createConnectionPool(
                    url+"?user="+username+"&password="+password);
            connection.connect();
            log.info("Proxysql connect successful!");
        }catch (Exception exception){
            log.error(exception.getMessage());
            System.exit(1);
        }
    }

    public List<MysqlServer> getAllServers(){
        try {
            CompletableFuture<QueryResult> future = connection.sendQuery(GET_ALL_SERVERS_SQL);
            QueryResult queryResult = future.get();
            List<JSONObject> jsonObjects = Utils.resultSets2ListJsonObj(queryResult.getRows(),true);
            return jsonObjects.stream().parallel().map( r -> {
                MysqlServer server = new MysqlServer();
                server.setHostgroup_id(r.getInteger("hostgroup_id"));
                server.setHostname(r.getString("hostname"));
                server.setPort(r.getInteger("port"));
                server.setGtid_port(r.getInteger("gtid_port"));
                server.setStatus(r.getString("status"));
                server.setWeight(r.getLong("weight"));
                server.setCompression(r.getInteger("compression"));
                server.setMax_connections(r.getInteger("max_connections"));
                server.setMax_replication_lag(r.getInteger("max_replication_lag"));
                server.setUse_ssl(r.getInteger("use_ssl"));
                server.setMax_latency_ms(r.getInteger("max_latency_ms"));
                server.setComment(r.getString("comment"));
                return server;
            }).collect(Collectors.toList());
        }catch (Exception exception){
            log.error(exception.getMessage());
            return null;
        }
    }

    public List<MysqlQueryRule> getAllRules(){
        try {
            CompletableFuture<QueryResult> future = connection.sendQuery(GET_ALL_RULES_SQL);
            QueryResult queryResult = future.get();
            List<JSONObject> jsonObjects = Utils.resultSets2ListJsonObj(queryResult.getRows(),true);
            return jsonObjects.stream().parallel().map( r ->{
                MysqlQueryRule mysqlQueryRule = new MysqlQueryRule();
                mysqlQueryRule.setRule_id(r.getInteger("rule_id"));
                mysqlQueryRule.setActive(r.getInteger("active"));
                mysqlQueryRule.setDestination_hostgroup(r.getInteger("destination_hostgroup"));
                mysqlQueryRule.setApply(r.getInteger("apply"));
                mysqlQueryRule.setNegate_match_pattern(r.getInteger("negate_match_pattern"));
                mysqlQueryRule.setRe_modifiers(r.getString("re_modifiers"));
                mysqlQueryRule.setAttributes(r.getString("attributes"));
                mysqlQueryRule.setFlagIN(r.getInteger("flagIN"));
                mysqlQueryRule.setMatch_digest(r.getString("match_digest"));
                mysqlQueryRule.setUsername(r.getString("username"));
                return mysqlQueryRule;
            }).collect(Collectors.toList());
        }catch (Exception exception){
            log.error(exception.getMessage());
            return null;
        }
    }

    public boolean addServer(MysqlServer server){
        try {
            CompletableFuture<QueryResult> future = connection.sendQuery(server.toInsertSqlString() + LOAD_SERVERS_TO_STORAGE);
            future.get().getStatusMessage();
            return true;
        }catch (Exception exception) {
            log.error(exception.getMessage());
            return false;
        }
    }

    public boolean addRule(MysqlQueryRule rule){
        try {
            CompletableFuture<QueryResult> future = connection.sendQuery(rule.toInsertSqlString() + LOAD_RULES_TO_STORAGE);
            future.get();
            return true;
        }catch (Exception exception) {
            log.error(exception.getMessage());
            return false;
        }
    }

    public boolean deleteServer(MysqlServer server){
        try {
            CompletableFuture<QueryResult> future = connection.sendQuery(server.toDeleteSqlString() + LOAD_SERVERS_TO_STORAGE);
            future.get();
            return true;
        }catch (Exception exception) {
            log.error(exception.getMessage());
            return false;
        }
    }

    public boolean deleteRule(MysqlQueryRule rule){
        try {
            CompletableFuture<QueryResult> future = connection.sendQuery(rule.toDeleteSqlString() + LOAD_RULES_TO_STORAGE);
            future.get();
            return true;
        }catch (Exception exception) {
            log.error(exception.getMessage());
            return false;
        }
    }
}
