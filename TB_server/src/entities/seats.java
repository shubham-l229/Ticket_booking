package entities;

import java.io.Serializable;

/**
 *
 * @author Aryan Mehta
 */
public class seats implements Serializable {

    private String seat_number;
    private String seat_status;
    private String seat_price;
    private String movie_name;

    public String getSeat_number() {
        return seat_number;
    }

    public String getSeat_price() {
        return seat_price;
    }

    public void setSeat_price(String seat_price) {
        this.seat_price = seat_price;
    }

    public void setSeat_number(String seat_number) {
        this.seat_number = seat_number;
    }

    public String getSeat_status() {
        return seat_status;
    }

    public void setSeat_status(String seat_status) {
        this.seat_status = seat_status;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public seats(String seat_number, String seat_status, String seat_price, String movie_name) {
        this.seat_number = seat_number;
        this.seat_status = seat_status;
        this.seat_price = seat_price;
        this.movie_name = movie_name;
    }

    @Override
    public String toString() {
        return "seats{" + "seat_number=" + seat_number + ", seat_status=" + seat_status + ", seat_price=" + seat_price + ", movie_name=" + movie_name + '}';
    }
}
