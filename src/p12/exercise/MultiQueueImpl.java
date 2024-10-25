package p12.exercise;

import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MultiQueueImpl<T, Q> implements MultiQueue<T, Q> {

    Map<Q, Queue<T>> queuesMap;

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
    private Queue<T> getRequiredQueue(Q queue) {
        Queue<T> q = this.queuesMap.get(queue);
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
    private List<T> peekAllElements(Q queue) {
        Queue<T> q = this.getRequiredQueue(queue);
        List<T> elements = new LinkedList<>();

        Iterator<T> queueIterator = q.iterator();
        while (queueIterator.hasNext()) {
            T e = queueIterator.next();
            elements.add(e);
        }
        return elements;
    }

    @Override
    public Set<Q> availableQueues() {
        return this.queuesMap.keySet();
    }

    @Override
    public void openNewQueue(Q queue) {
        if (this.queuesMap.containsKey(queue)) {
            throw new IllegalArgumentException("Qeueue " + queue + "is already available");
        }
        this.queuesMap.put(queue, new LinkedList<T>());
    }

    @Override
    public boolean isQueueEmpty(Q queue) {
        Queue<T> q = this.getRequiredQueue(queue);
        return q.isEmpty();
    }

    @Override
    public void enqueue(T elem, Q queue) {
        Queue<T> q = this.getRequiredQueue(queue);
        q.offer(elem);
    }

    @Override
    public T dequeue(Q queue) {
        Queue<T> q = this.getRequiredQueue(queue);
        return q.poll();
    }

    @Override
    public Map<Q, T> dequeueOneFromAllQueues() {
        Map<Q, T> dequeuedValsMap = new HashMap<>();
        for (Q queue : this.queuesMap.keySet()) {
            Queue<T> q = this.getRequiredQueue(queue);
            T val = q.poll();
            dequeuedValsMap.put(queue, val);
        }
        return dequeuedValsMap;
    }

    @Override
    public Set<T> allEnqueuedElements() {
        Set<T> enqueuedElementsSet = new HashSet<>();
        for (Q queue : this.queuesMap.keySet()) {
            List<T> elements = this.peekAllElements(queue);
            enqueuedElementsSet.addAll(elements);
        }
        return enqueuedElementsSet;
    }

    @Override
    public List<T> dequeueAllFromQueue(Q queue) {
        Queue<T> q = this.getRequiredQueue(queue);
        List<T> elements = new LinkedList<>();
        while (!q.isEmpty()) {
            elements.add(q.poll());
        }
        return elements;
    }

    @Override
    public void closeQueueAndReallocate(Q queue) {
        List<T> elements = dequeueAllFromQueue(queue);
        for (Q key : this.queuesMap.keySet()) {
            if (key != queue) {
                Queue<T> q = this.getRequiredQueue(key);
                q.addAll(elements);
                this.queuesMap.remove(queue);
                return;
            }
        }
        throw new IllegalStateException("No alternative found for elements of queue " + queue);
    }
}
