package graphs;

import java.util.Iterator;

/**
 * A doubly ended singly linked list. Supports adding elements at the front and
 * end, retrieving by index, checking size, and iterating over elements.
 *
 * @param <E> the type of elements stored in the list
 */
public class SinglyLinkedList<E> implements Iterable<E> {

    private Node<E> head;       // first node containing the first item in the linked list
    private Node<E> tail;       // last node containing the last item in the linked list
    private int size;           // current number of items in the linked list

    /**
     * Inner class representing a node in the singly linked list.\ Singlely
     * linked lists only move in one direction.
     */
    private static class Node<E> {

        // Defines data to hold value and "next" to point to the next node.
        E data;
        Node<E> next;

        Node(E data) {
            this.data = data;
        }
    }

    public SinglyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Adds an item to the end of the list
     *
     * @param item the element to add
     */
    public void add(E item) {
        // Creates new node and updates the tail (and head if empty).
        Node<E> newNode = new Node<>(item);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }

        // Tracks list length
        size++;
    }

    /**
     * Adds an item to the beginning of the list
     *
     * @param item the element to add
     */
    public void addFirst(E item) {
        // Creates new node pointing to current head and updates head reference.
        Node<E> newNode = new Node<>(item);

        if (head == null) {
            tail = newNode;
        }
        newNode.next = head;
        head = newNode;

        // Tracks list length
        size++;
    }

    /**
     * Gets the element at the specified index
     *
     * @param index the index of the element to retrieve
     * @return the item at the given position
     * @throws IndexOutOfBoundsException if index is not within range
     */
    public E get(int index) {
        // Validates index bounds and traverses the list from head using a loop to find the target node.
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<E> current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        return current.data;
    }

    /**
     * @return the number of elements in the list
     *
     */
    public int size() {
        return this.size;
    }

    /**
     * Returns an iterator over the elements in this list.
     *
     * @return an {@link Iterator} over the elements in this list
     * @throws java.util.NoSuchElementException if the {@link Iterator#next()}
     * method is called when no more elements are available
     */
    @Override
    public Iterator<E> iterator() {
        // Custom Iterator that traverses nodes sequentially without copying data.
        // This allows the list to be used in for each loops. This is more memory efficient (walks over the existing nodes one by one).
        return new Iterator<E>() {
            private Node<E> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                E data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}
