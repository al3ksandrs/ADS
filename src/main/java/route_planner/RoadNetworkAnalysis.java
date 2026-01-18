package route_planner;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class that provides analysis methods for junctions (cities) and
 * roads.
 */
public class RoadNetworkAnalysis {

    /**
     * Represents the road network as a mapping between junctions and roads.
     */
    private final Map<Junction, Road> roadNetwork;

    public RoadNetworkAnalysis(Map<Junction, Road> connections) {
        this.roadNetwork = connections;
    }

    /**
     * Finds all cities (junctions) in the same province as the given city.
     * Returns an empty list if no cities match.
     */
    public List<Junction> citiesInSameProvince(Junction city) {
        if (city == null || city.getProvince() == null) {
            return List.of();
        }

        return roadNetwork.keySet().stream()
                .filter(junction -> city.getProvince().equals(junction.getProvince())) // same province
                .filter(junction -> !junction.equals(city)) // exclude the given city
                .toList(); // collect to list
    }

    /**
     * Calculates the total length of all roads.
     */
    public double totalRoadLength() {
        return roadNetwork.values().stream()
                .mapToDouble(Road::getLength) // get lengths
                .sum(); // sum lengths
    }

    /**
     * Returns the names of the top 5 most populated cities in the road network.
     */
    public List<String> top5CityNamesByPopulation() {
        return roadNetwork.keySet().stream()
                .sorted(Comparator.comparingInt(Junction::getPopulation).reversed()) // sort by population descending
                .limit(5) // take top 5
                .map(Junction::getName) // get names
                .toList(); // collect to list
    }

    /**
     * Finds the total length of all roads starting from cities with population
     * above a threshold.
     */
    public double totalLengthFromBigCities(int minPopulation) {
        return roadNetwork.entrySet().stream()
                .filter(e -> e.getKey().getPopulation() > minPopulation) // filter big cities
                .mapToDouble(e -> e.getValue().getLength()) // get road lengths
                .sum(); // sum lengths
    }

    /**
     * Calculates the total length of all roads for each province.
     *
     * @return a map where the key is the province name and the value is the
     * total length of roads in that province; provinces with no roads will not
     * appear in the map.
     */
    public Map<String, Double> totalRoadLengthPerProvince() {
        return roadNetwork.entrySet().stream()
                .collect(Collectors.groupingBy( // group by province
                        e -> e.getKey().getProvince(), // key: province
                        Collectors.summingDouble(e -> e.getValue().getLength()) // sum lengths
                ));
    }

    /**
     * Returns all roads where the speed limit is higher than the average speed
     * limit.
     */
    public List<Road> roadsFasterThanAverage() {
        double averageSpeed = roadNetwork.values().stream()
                .mapToInt(Road::getMaxSpeed) // get max speeds
                .average() // compute average
                .orElse(0.0); // default to 0 if no roads

        return roadNetwork.values().stream() 
                .filter(r -> r.getMaxSpeed() > averageSpeed) // filter faster than average
                .toList(); // collect to list
    }

    /**
     * Finds all provinces that have more than X cities. Use
     * Collectors.groupingBy(), Collectors.counting(),
     */
    public List<String> provincesWithMoreThanXCities(int x) {
        return roadNetwork.keySet().stream()
                .collect(Collectors.groupingBy(Junction::getProvince, Collectors.counting())) // group and count
                .entrySet().stream() // convert to stream
                .filter(e -> e.getValue() > x) // filter by count
                .map(Map.Entry::getKey) // get province names
                .toList(); // collect to list
    }
}
