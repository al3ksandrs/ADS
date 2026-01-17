package graphs;

import java.util.Comparator;
import java.util.List;

public class Sorter<E> {

    /**
     * Sorts all items by insertion sort using the provided comparator
     * for deciding relative ordening of two items.
     * Items are sorted 'in place' without use of an auxiliary list or array
     *
     * @param items
     * @param comparator
     * @return the items sorted in place
     */
    public List<E> insertionSort(List<E> items, Comparator<E> comparator) {
        // a list of 0 or 1 items is already sorted
        if (items == null || items.size() <= 1) {
            return items;
        }

        // Iterate through the list starting from the second element
        for (int i = 1; i < items.size(); i++) {
            E key = items.get(i); // The item we want to insert into the sorted portion
            int j = i - 1;

            // Moves elements that are greater than key to one position ahead of their current position
            while (j >= 0 && comparator.compare(items.get(j), key) > 0) {
                items.set(j + 1, items.get(j));
                j--;
            }

            // Places the key in its correct position
            items.set(j + 1, key);
        }

        return items;
    }

    /**
     * Sorts all items by quick sort using the provided comparator
     * for deciding relative ordening of two items.
     * Items are sorted 'in place' without use of an auxiliary list or array
     *
     * @param items
     * @param comparator
     * @return the items sorted in place
     */
    public List<E> quickSort(List<E> items, Comparator<E> comparator) {
        if (items != null && items.size() > 1) {
            quickSortRecursive(items, 0, items.size() - 1, comparator);
        }
        return items;
    }

    // Helper method to performth e recursive sort steps
    private void quickSortRecursive(List<E> items, int low, int high, Comparator<E> comparator) {
        if (low < high) {
            // Partition the list and get the pivot index
            int pivotIndex = partition(items, low, high, comparator);

            // Sort elements before and after partition
            quickSortRecursive(items, low, pivotIndex - 1, comparator);
            quickSortRecursive(items, pivotIndex + 1, high, comparator);
        }
    }

    // Helper method to partition the list around a pivot (Lomuto partition scheme)
    private int partition(List<E> items, int low, int high, Comparator<E> comparator) {
        E pivot = items.get(high); // Chooses the last element as the pivot
        int i = (low - 1); // Index of smaller element

        for (int j = low; j < high; j++) {
            // If current element is smaller than or equal to pivot
            if (comparator.compare(items.get(j), pivot) <= 0) {
                i++;
                swap(items, i, j);
            }
        }

        // Places the pivot element in the correct position
        swap(items, i + 1, high);
        return i + 1;
    }

    // Utility method to swap two elements in the list
    private void swap(List<E> items, int i, int j) {
        E temp = items.get(i);
        items.set(i, items.get(j));
        items.set(j, temp);
    }
}
