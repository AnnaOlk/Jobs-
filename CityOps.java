import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CityOps {

    public static void main(String args[]) throws SQLException {
        CityOps h = new CityOps();
        h.run(args);
    }

    private void run(String[] args) throws SQLException {
        Config config = new Config(args);

        Connection con = DriverManager.getConnection(
                "jdbc:mysql://" + config.getConnStr() + "?useSSL=false",
                config.getUserName(), config.getPassword()
        );

        List<CityDO> cityList= getCities(con);

        for(CityDO city: cityList) {
            System.out.printf("cityId = %d, cityName = %s, population = %d\n",
                    city.getCityId(), city.getCityName(), city.getPopulation());
        }

        testSQLUpdates(con);

        con.close();
    }

    List<CityDO> getCities(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select city_id, city, population from city order by city");

        List<CityDO> cityList = new ArrayList<>();

        while (rs.next()) {
            int cityId = rs.getInt("city_id");
            String cityName = rs.getString("city");
            long population = rs.getLong("population");
            CityDO cityDO = new CityDO(cityId, cityName, population);
            cityList.add(cityDO);
        }

        rs.close();
        stmt.close();

        return cityList;
    }

    int insertCities(Connection con, int cityId, String cityName, long population) throws SQLException {
        try(PreparedStatement pstate = con.prepareStatement("insert into city (city_id, city, population) values ( ? ,?, ?)")) {
            pstate.setInt(1, cityId);
            pstate.setString(2, cityName);
            pstate.setLong(3, population);
            return pstate.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error occurred during insertion");
            throw e;
        }
    }

    int deleteCities(Connection con, int cityId) throws SQLException {
        try(PreparedStatement pstate = con.prepareStatement("delete from city where city_id = ?")) {
            pstate.setInt(1, cityId);
            return pstate.executeUpdate();
        } catch (SQLException e) {
            System.out.println("No row with such cityId exists");
            throw e;
        }
    }

    // update cities set city_name = ?, population = ? where city_id = ?;

    int updateCities(Connection con, int cityId, String cityName, Long population) throws SQLException{
        int count = 1;
        String sql = "Update city set ";
        String separator = " ";
        if(cityName != null){
            sql += "city = ?";
            separator = ", ";
        }
        if(population != null) {
            sql += separator + "population = ?";
        }

        sql += " WHERE city_id = ?";

        if(cityName == null && population == null) {
            return -1;
        }

        PreparedStatement pstate = con.prepareStatement(sql);
        if(cityName != null){
            pstate.setString(count++, cityName);
        }
        if(!Objects.isNull(population)) {
            pstate.setLong(count++, population);
        }
        pstate.setInt(count, cityId);

        return pstate.executeUpdate();
    }

    void testSQLUpdates(Connection conn) throws SQLException{
        List<CityDO> cityList;
        int cityId = -5;
        String cityName = "Albuquerque";
        String updatedName = "Not Albuquerque";
        Long population = 7L;
        Long updatedpop = 700L;
        boolean found = false;

        int insertResult = insertCities(conn, cityId, cityName, population);
        cityList = getCities(conn);
        for(CityDO city: cityList) {
            if(city.getCityId() == cityId){
                found = true;
            }
        }
        if(found && insertResult == 1) {
            System.out.println("Parameters inserted properly");
        } else {
            System.out.println("Parameters not inserted properly");
            conn.rollback();
            return;
        }

        // -------------update---------------------------------------------------------

        found = false;
        int updateResult = updateCities(conn, cityId, null, null);
        cityList = getCities(conn);
        for(CityDO city: cityList) {
            if (city.getCityId() == cityId && city.getCityName().equals(cityName) && city.getPopulation() == population) {
                found = true;
            }
        }
        if(updateResult == -1 && found) {
            System.out.println("void update test passed");
        } else {
            System.out.println("void update test not passed");
        }
        found = false;
        updateCities(conn, cityId, cityName, population);
        updateResult = updateCities(conn, cityId, null, updatedpop);
        cityList = getCities(conn);
        for(CityDO city: cityList) {
            if (city.getCityId() == cityId && city.getCityName().equals(cityName) && city.getPopulation() == updatedpop) {
                found = true;
            }
        }
        if(updateResult == 1 && found) {
            System.out.println("void cityName test passed");
        } else {
            System.out.println("void cityName test failed");
        }

        found = false;
        updateResult = updateCities(conn, cityId, updatedName, null);
        cityList = getCities(conn);
        for(CityDO city: cityList) {
            if (city.getCityId() == cityId && city.getCityName().equals(updatedName) && city.getPopulation() == updatedpop) {
                found = true;
            }
        }
        if(updateResult == 1 && found) {
            System.out.println("void population test passed");
        } else {
            System.out.println("void population test failed");
        }

        found = false;
        updateResult = updateCities(conn, cityId, cityName, population);
        cityList = getCities(conn);
        for(CityDO city: cityList) {
            if (city.getCityId() == cityId && city.getCityName().equals(cityName) && city.getPopulation() == population) {
                found = true;
            }
        }
        if(updateResult == 1 && found) {
            System.out.println("all parameter update passed");
        } else {
            System.out.println("all parameter update failed");
        }

        //-----delete-----------------------------------------------------------------------------

        int deleteResult = deleteCities(conn, cityId);
        cityList = getCities(conn);
        for(CityDO city: cityList) {
            if(city.getCityId() == cityId){
                System.out.println("Parameters not deleted properly");
                conn.rollback();
                return;
            }
        }
        if(deleteResult == 1) {
            System.out.println("Parameters properly deleted");
        }
    }
}



