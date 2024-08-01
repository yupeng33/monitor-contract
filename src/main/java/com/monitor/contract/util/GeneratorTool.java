package com.monitor.contract.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * <p>
 * mybatis generator tool
 * </p>
 */
public class GeneratorTool {

    /**
     * 代码生成器配置文档：https://baomidou.com/pages/981406/
     */
    public static void main(String[] args) throws SQLException {
        Scanner scan = new Scanner(System.in);
        String defaultHost = "localhost";
        String defaultPort = "3306";
        String defaultDatabase = "test";
        String defaultUsername = "root";
        String defaultPassword = "1234567890";
        String moduleName = null;

        System.out.println("请输入mysql host,(defaultHost :" + defaultHost + ")");
        String host = scan.nextLine();
        if (StringUtils.isEmpty(host)) host = defaultHost;

        System.out.println("请输入mysql port,(defaultPort :" + defaultPort + ")");
        String port = scan.nextLine();
        if (StringUtils.isEmpty(port)) port = defaultPort;

        System.out.println("请输入mysql database,(defaultDatabase :" + defaultDatabase + ")");
        String database = scan.nextLine();
        if (StringUtils.isEmpty(database)) database = defaultDatabase;

        System.out.println("请输入mysql username,(username :" + defaultUsername + ")");
        String username = scan.nextLine();
        if (StringUtils.isEmpty(username)) username = defaultUsername;

        System.out.println("请输入mysql password,(password :" + defaultPassword + ")");
        String password = scan.nextLine();
        if (StringUtils.isEmpty(password)) password = defaultPassword;

        generate(moduleName, host, port, database, username, password);

    }

    private static void generate(String moduleName, String host, String port, String database, String username, String password) {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;

        DataSourceConfig.Builder dataSourceConfig = new DataSourceConfig.Builder(jdbcUrl, username, password);

        FastAutoGenerator.create(dataSourceConfig)
                // 全局配置
                .globalConfig((scanner, builder) -> builder
                        .author("peng").outputDir("src/main/java").disableOpenDir().fileOverride())
                // 包配置
                .packageConfig((scanner, builder) ->
                        builder.moduleName(moduleName).parent("com.monitor.contract").controller("controller").mapper("mapper").xml("mapper").entity("model.entity"))
                // 策略配置
                .strategyConfig((scanner, builder) -> {
                    builder.controllerBuilder().enableRestStyle();
                    builder.addInclude(scanner.apply("请输入表名，多个表名用,隔开"));
                    builder.entityBuilder().enableLombok();
                }).templateEngine(new FreemarkerTemplateEngine()).execute();
    }
}
