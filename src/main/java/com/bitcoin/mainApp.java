package com.bitcoin;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Scanner;

@SpringBootApplication
public class mainApp {
    public static String port;

    public static void main(String[] args) {
        // 监听控制台,获取用户输入的内容,作为springboot启动时使用的端口号
        Scanner scanner = new Scanner(System.in);
        port = scanner.nextLine();
        new SpringApplicationBuilder(mainApp.class).properties("server.port=" + port).run(args);
    }
}
