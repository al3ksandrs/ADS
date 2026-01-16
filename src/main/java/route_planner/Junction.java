package route_planner;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;

import graphs.Identifiable;

// The junction class represents a town/crossing where multiple roads come together.
// In the directed graph, junctions are represented as Vertices (nodes).
public class Junction implements Identifiable
{

    private String name;            // unique name of the junction
    private double locationX;       // RD x-coordinate in km
    private double locationY;       // RD y-coordinate in km
    private String province;
    private int population;

    public Junction(Builder builder) {
        name = builder.name;
        locationX = builder.locationX;
        locationY = builder.locationY;
        province = builder.province;
        population = builder.population;
    }

    public Junction(String name) {
        this.name = name;
    }

    // TODO: more implementations as required for use with DirectedGraph, HashSet and/or HashMap
    // -----------------------------
    // Public getters and setters
    // -----------------------------
    public String getName() {
        return name;
    }

    public double getLocationX() {
        return locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public String getProvince() {
        return province;
    }

    public int getPopulation() {
        return population;
    }

    // -----------------------------
    // Utility methods
    // -----------------------------
    /**
     * calculates the carthesion distance between two junctions
     *
     * @param target
     * @return
     */
    double getDistance(Junction target) {
        // Calculate the cartesion distance between this and the target junction
        // using the locationX and locationY as provided in the dutch RD-coordinate system
        double dX = target.locationX - locationX;
        double dY = target.locationY - locationY;
        return Math.sqrt(dX * dX + dY * dY);
    }

    // Returns the junction's name as its unique identifier.
    @Override
    public String getId() {
        return name;
    }

    // To define when two Junction objects are considered equal.
    // Otherwise two Junctions with the same name would be considered different objects.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Junction junction = (Junction) o;
        return Objects.equals(name, junction.name);
    }

    // Returns a hash code value for the junction based on its name.
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    /**
     * draws the junction onto a svg image with a given colour
     *
     * @param svgWriter
     * @param colour
     */
    public void svgDraw(PrintStream svgWriter, String colour) {
        // calculate the size of the dot relative to population at the junction
        double radius = 0.1 + 0.3 * Math.log(1 + population / 2000);
        //radius = 0.1;
        int fontSize = 3;

        // accounts for the reversed y-direction of the svg coordinate system relative to RD-coordinates
        svgWriter.printf(Locale.ENGLISH, "<circle cx='%.3f' cy='%.3f' r='%.3f' fill='%s'/>\n",
                locationX, -locationY, radius, colour);
        svgWriter.printf(Locale.ENGLISH, "<text x='%.3f' y='%.3f' font-size='%d' fill='%s' text-anchor='middle'>%s</text>\n",
                locationX, -locationY - 1.3, fontSize, colour, name);

    }

    /**
     * Draws the road segment onto a .svg image with the specified colour If no
     * colour is provided, a default will be calculated on the basis of the
     * maxSpeed
     *
     * @param svgWriter
     * @param colour
     */
    public void svgDrawRoad(PrintStream svgWriter, Junction from, double width, String colour) {
        if (from == null) {
            return;
        }
        // accounts for the reversed y-direction of the svg coordinate system relative from RD-coordinates
        svgWriter.printf(Locale.ENGLISH, "<line x1='%.3f' y1='%.3f' x2='%.3f' y2='%.3f' stroke-width='%.3f' stroke='%s'/>\n",
                this.getLocationX(), -this.getLocationY(),
                from.getLocationX(), -from.getLocationY(),
                width, colour);
    }

    /**
     * Builder class for creating Junction objects in a readable and flexible
     * way.
     */
    public static class Builder {

        private String name;
        private double locationX;
        private double locationY;
        private String province;
        private int population;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder location(double locationX, double locationY) {
            this.locationX = locationX;
            this.locationY = locationY;
            return this;
        }

        public Builder province(String province) {
            this.province = province;
            return this;
        }

        public Builder population(int population) {
            this.population = population;
            return this;
        }

        public Junction build() {
            return new Junction(this);
        }
    }

    // -----------------------------
    // Object overrides
    // -----------------------------
    @Override
    public String toString() {
        return name;
    }

}
