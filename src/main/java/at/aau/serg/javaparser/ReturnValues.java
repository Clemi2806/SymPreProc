package at.aau.serg.javaparser;

import java.util.*;

public class ReturnValues implements Iterable<Object>{
    private List<Object> values;

    public ReturnValues() {
        this.values = new ArrayList<>();
    }

    public ReturnValues(List<Object> values) {
        this.values = values;
    }

    public ReturnValues(Object... values) {
        this.values = Arrays.asList(values);
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(values);
    }

    public int size() {
        return values.size();
    }

    @Override
    public Iterator<Object> iterator() {
        return values.iterator();
    }
}
