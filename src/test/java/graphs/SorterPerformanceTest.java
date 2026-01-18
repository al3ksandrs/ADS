package graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Performance benchmarking utility for comparing Insertion Sort vs QuickSort algorithms.
 * Measures execution times across varying dataset sizes for empirical complexity analysis.
 * <p>
 * Note: This is not a traditional unit test but a timing measurement tool that
 * generates data for external performance analysis and Big-O verification.
 * <p>
 * Key features:
 * - Measures runtime from 100 to 5,000,000 elements (or 20-second timeout)
 * - Ensures fair comparison through dataset replication
 * - Validates sorting correctness against Collections.sort()
 * - Controls JVM state for consistent timing measurements
 */
public class SorterPerformanceTest {

    private static final long SEED = 20211220L;
    private static final Random RANDOM = new Random(SEED);


    @Test
    void measureEfficiency() {
        Sorter<Country> sorter = new Sorter<>();
        // Comparator to sort by population
        Comparator<Country> byPopulation = Comparator.comparingInt(Country::getPopulation)
        .thenComparing(Country::getId);

        System.out.printf("%-10s %-20s %-20s%n", "N", "Insertion sort (ms)", "Quick sort (ms)");
        System.out.println("-------------------------------------------------------");

        boolean runInsertion = true;
        boolean runQuick = true;
        long timeLimit = 20000; // 20 seconds

        for (int n = 100; n <= 5_000_000; n *= 2) {
            // Stops if both algorithms have exceeded the time limit
            if (!runInsertion && !runQuick) {
                break;
            }

            // Generates base dataset
            List<Country> baseData = generateCountryDataset(n);

            // Prepares validation set (Ground truth)
            List<Country> validationSet = new ArrayList<>(baseData);
            Collections.sort(validationSet, byPopulation);

            String insertionTimeOutput = "-";
            String quickTimeOutput = "-";

            // Benchmark for insertion sort
            if (runInsertion) {
                List<Country> data = new ArrayList<>(baseData);
                
                System.gc(); // Cleans memory before run
                long start = System.currentTimeMillis();
                
                sorter.insertionSort(data, byPopulation);
                
                long duration = System.currentTimeMillis() - start;

                // Validates correctness
                assertEquals(validationSet, data, "Insertion sort failed correctness check at N=" + n);

                insertionTimeOutput = String.valueOf(duration);

                // Stops if it takes too long
                if (duration > timeLimit) {
                    runInsertion = false;
                }
            }

            // Benchmark for quick sort
            if (runQuick) {
                List<Country> data = new ArrayList<>(baseData);
                
                System.gc(); // Cleans memory before run
                long start = System.currentTimeMillis();
                
                sorter.quickSort(data, byPopulation);
                
                long duration = System.currentTimeMillis() - start;

                // Validates correctness
                assertEquals(validationSet, data, "Quick sort failed correctness check at N=" + n);

                quickTimeOutput = String.valueOf(duration);

                // Stops if it takes too long
                if (duration > timeLimit) {
                    runQuick = false;
                }
            }

            System.out.printf("%-10d %-20s %-20s%n", n, insertionTimeOutput, quickTimeOutput);
        }
    }


    // Private helper method
    private List<Country> generateCountryDataset(int size) {
        List<Country> countries = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String name = "Country_" + String.format("%06d", i);
            int population = RANDOM.nextInt(100_000_000) + 1;
            countries.add(new Country(name, population));
        }
        Collections.shuffle(countries, RANDOM); // same seed = same shuffle
        return countries;
    }

}
