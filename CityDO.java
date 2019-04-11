public class CityDO {
    private int cityId;
    private String cityName;
    private long population;

    CityDO(int cityId, String cityName, long population) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.population = population;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public long getPopulation() {
        return population;
    }
}
