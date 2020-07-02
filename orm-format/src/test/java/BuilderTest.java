import org.junit.Test;
import org.zhiyong.format.builder.ParameterFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BuilderTest {

    @Test
    public void parameterBuilderTest(){
//        ParameterBuilder builder = ParameterBuilder.from("CREATE TABLE :table(:fields)");
//        builder.by("fields", "name varchar(50)", new String[]{"1", "2", "3"}, (f, e)->{
//
//        }).by("vd", (f, e)->{
//            f.set("table", e);
//        });

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection =
                    DriverManager.getConnection("jdbc:mysql://localhost:3306/test2?user=root&password=79982473");
            System.out.println(ParameterFactory.prepared("DELETE FROM :table WHERE :column=:value", connection)
                    .set("friend")
                    .set("origin_id")
                    .setByValue("李四")
                    .transform()
                    .executeUpdate());
            System.out.println("success");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }






    }
}
