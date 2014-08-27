package sk.lazyman.gizmo.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lazyman
 */
public class PrivateDrive {

    private long id;

    private String destination;

    private double length;

    private User driver;

    private String car;

    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public Date getDate() {
        return date;
    }

    public String getDateAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("E dd.MMM.yyyy");
        return sdf.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
