package at.aau.serg.javaparser;

import java.util.*;
import java.util.stream.Collectors;

public class ReturnValues {
    Map<Class, Object> objects = new HashMap<>();

    public ReturnValues() {}

    public ReturnValues(Object... objects) {
        Arrays.stream(objects).forEach(this::addObject);
    }

    public ReturnValues(List<Object> objects) {
        objects.forEach(this::addObject);
    }

    public void addObject(Object object) {
        if (object == null) return;
        objects.put(object.getClass(), object);
    }

    public Map<Class, Object> getObjectMap() {
        return objects;
    }

    public Set<Map.Entry<Class, Object>> getEntrySet() {
        return objects.entrySet();
    }

    public List<Object> getObjects() {
        return new ArrayList<>(objects.values());
    }
}
