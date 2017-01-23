package mn.factory.testanimation.testanimation.models;

/**
 * Created by pogodaev on 17.01.17.
 */

public class FinanceModel {

    private String name;
    private float price;
    private float diff;
    private boolean grow;
    private boolean fav;

    public FinanceModel(String name, float price, float diff, boolean grow, boolean fav){
        this.name = name;
        this.price = price;
        this.diff = diff;
        this.grow = grow;
        this.fav = fav;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return String.valueOf(price);
    }

    public String getDiff() {
        return String.valueOf(diff) + " %";
    }

    public boolean isGrow() {
        return grow;
    }

    public boolean isFav() {
        return fav;
    }
}
