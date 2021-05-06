package br.com.infox.dal;

import java.sql.Connection;
import java.sql.DriverManager;

public class ModuloConexao {

    public static Connection conector() {

        Connection conexao = null;
        String driver = "com.mysql.cj.jdbc.Driver";
        //String driver = "org.postgresql.Driver";
        //The new driver class is `com.mysql.cj.jdbc.Driver'. The driver 
        //is automatically registered via the SPI and manual loading of the driver 
        //class is generally unnecessary
        String url = "jdbc:mysql://localhost:3306/dbinfox"; //MYSQL
        //String url = "jdbc:postgresql://localhost:5432/dbinfox"; // POSTGRESQL
        String user = "root"; //MYSQL USER
        //String user = "postgres"; // POSTGRESQL USER
        String password = ""; // MYSQL PASSWORD
        //String password = "admin"; //POSTGRES PASSWORD
        
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
    
        } catch (Exception e) {
            System.out.println("falha de comunicação com o servidor. " + 
                                "verifique se o servidor está operante." +
                                "erro: " + e);
            return null;
        }

    }

}
