package it.italiandudes.hackathon2025.data;

import java.util.LinkedList;
import java.util.List;

public final class FixedSizeQueue<T> {

    // Attributes
    private final int maxSize;
    private final LinkedList<T> queue;

    // Constructors
    public FixedSizeQueue(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new LinkedList<>();
    }

    // Methods
    public int size() {
        return queue.size();
    }
    public T peek() {
        return queue.peek();
    }
    public T poll() {
        return queue.poll();
    }
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    public void clear() {
        queue.clear();
    }
    public T getLast() {
        return queue.getLast();
    }
    public T getFirst() {
        return queue.getFirst();
    }
    public List<T> getDataList() {
        return queue.stream().toList();
    }
    public void add(T value) {
        if (queue.size() >= maxSize) {
            queue.removeFirst();
        }
        queue.addLast(value);
    }
    @Override
    public String toString() {
        return queue.toString();
    }
}
