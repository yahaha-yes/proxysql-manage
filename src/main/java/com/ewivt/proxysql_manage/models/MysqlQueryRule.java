package com.ewivt.proxysql_manage.models;

import lombok.Data;

@Data
public class MysqlQueryRule {
    private int rule_id;
    private int destination_hostgroup;
    private int apply = 1;
    private int negate_match_pattern;
    private String re_modifiers = "CASELESS";
    private int active = 1;
    private String attributes;
    private int flagIN;
    private String match_digest;
    private String username;

    public String toInsertSqlString(){
        if (destination_hostgroup==0){
            return "";
        }
        if (rule_id!=0){
            return String.format("insert into mysql_query_rules(rule_id,active,username,match_digest,destination_hostgroup,apply) " +
                    "values (%d, %d , '%s' , '%s' , %d , %d );",rule_id,apply,username,match_digest,destination_hostgroup,apply);
        }
        return String.format("insert into mysql_query_rules(active,username,match_digest,destination_hostgroup,apply) " +
                "values ( %d , '%s' , '%s' , %d , %d );",apply,username,match_digest,destination_hostgroup,apply);
    }

    public String toDeleteSqlString(){
        if (rule_id==0){
            return "";
        }
        return String.format("delete from mysql_query_rules where rule_id = %d;",rule_id);
    }
}
