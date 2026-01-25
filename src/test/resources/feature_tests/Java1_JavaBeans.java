// Java 1.1 feature: JavaBeans API
// Expected Version: 1
// Required Features: JAVABEANS
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.BeanInfo;
import java.beans.Introspector;

public class Java1_JavaBeans {
    // A simple JavaBean with property change support
    private String name;
    private int age;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        pcs.firePropertyChange("name", oldName, name);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        int oldAge = this.age;
        this.age = age;
        pcs.firePropertyChange("age", oldAge, age);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void testIntrospection() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(Java1_JavaBeans.class);
        System.out.println("Properties: " + beanInfo.getPropertyDescriptors().length);
    }
}