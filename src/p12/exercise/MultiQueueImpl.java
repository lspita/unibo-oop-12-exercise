package p12.exercise;

import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MultiQueueImpl<T, Q> implements MultiQueue<T, Q> {

    final private Map<Q, Queue<T>> queuesMap;

    /**
     * Initialize empty {@link MultiQueueImpl}
     */
    public MultiQueueImpl() {
        this.queuesMap = new HashMap<>();
    }

    /**
     * Return queue with key {@code queue}
     * 
     * @param queue key of queue to return
     * @return associated queue
     * @throws IllegalArgumentException if key {@code queue} as not associated queue
     */
    private Queue<T> getRequiredQueue(final Q queue) {
        final Queue<T> q = this.queuesMap.get(queue);
        if (q == null) {
            throw new IllegalArgumentException("Queue " + queue + "is not available");
        }
        return q;
    }

    /**
     * Return list of queue elements without removing them
     * 
     * @param queue key of queue to peek
     * @return list of peeked elements
     * @throws IllegalArgumentException if key {@code queue} as not associated queue
     */
    private List<T> peekAllElements(final Q queue) {
        final Queue<T> q = this.getRequiredQueue(queue);
        final List<T> elements = new LinkedList<>(q);
        return elements;
    }

    @Override
    public Set<Q> availableQueues() {
        return this.queuesMap.keySet();
    }

    @Override
    public void openNewQueue(final Q queue) {
        if (this.queuesMap.containsKey(queue)) {
            throw new IllegalArgumentException("Qeueue " + queue + "is already available");
        }
        this.queuesMap.put(queue, new LinkedList<T>());
    }

    @Override
    public boolean isQueueEmpty(final Q queue) {
        final Queue<T> q = this.getRequiredQueue(queue);
        return q.isEmpty();
    }

    @Override
    public void enqueue(final T elem, final Q queue) {
        final Queue<T> q = this.getRequiredQueue(queue);
        q.offer(elem);
    }

    @Override
    public T dequeue(final Q queue) {
        final Queue<T> q = this.getRequiredQueue(queue);
        return q.poll();
    }

    @Override
    public Map<Q, T> dequeueOneFromAllQueues() {
        final Map<Q, T> dequeuedValsMap = new HashMap<>();
        for (final Q queue : this.queuesMap.keySet()) {
            final Queue<T> q = this.getRequiredQueue(queue);
            final T val = q.poll();
            dequeuedValsMap.put(queue, val);
        }
        return dequeuedValsMap;
    }

    @Override
    public Set<T> allEnqueuedElements() {
        final Set<T> enqueuedElementsSet = new HashSet<>();
        for (final Q queue : this.queuesMap.keySet()) {
            final List<T> elements = this.peekAllElements(queue);
            enqueuedElementsSet.addAll(elements);
        }
        return enqueuedElementsSet;
    }

    @Override
    public List<T> dequeueAllFromQueue(final Q queue) {
        final Queue<T> q = this.getRequiredQueue(queue);
        final List<T> elements = new LinkedList<>();
        while (!q.isEmpty()) {
            elements.add(q.poll());
        }
        return elements;
    }

    @Override
    public void closeQueueAndReallocate(final Q queue) {
        final List<T> elements = dequeueAllFromQueue(queue);
        for (final Q key : this.queuesMap.keySet()) {
            if (key != queue) {
                final Queue<T> q = this.getRequiredQueue(key);
                q.addAll(elements);
                this.queuesMap.remove(queue);
                return;
            }
        }
        throw new IllegalStateException("No alternative found for elements of queue " + queue);
    }
}
