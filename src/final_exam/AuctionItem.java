package final_exam;

import java.io.Serializable;
import java.time.*;

public class AuctionItem implements Serializable {
    private String name;
    private int bid;
    private int price;
    private boolean isSold;
    private int pagePosition; // position on each clients grid map
    private LocalTime timeRemaining;
    private final LocalTime startTime;
    private final int buyNowPrice;

    AuctionItem(String name, int startingPrice, int pagePosition, int buyNowPrice){
        this.timeRemaining = LocalTime.of(10, 0);
        this.startTime = LocalTime.now();
        this.name = name;
        this.price = startingPrice;
        this.pagePosition = pagePosition;
        this.isSold = false;
        this.buyNowPrice = buyNowPrice;
        this.bid = 0;
    }

    public void setPrice(int bid){
        this.price = bid;
        this.bid = 0;
    }

    public int getPrice(){
        return price;
    }

    public void sold(){
        this.isSold = true;
    }

    public boolean isSold(){
        return this.isSold;
    }

    public String getName(){
        return this.name;
    }

    public void setBid(int bid){
        this.bid = bid;
    }

    public int getBid(){
        return bid;
    }

    public void setPagePosition(int position){
        this.pagePosition = position;
    }
    public int getPagePosition(){
        return this.pagePosition;
    }

    public LocalTime getTimeRemaining(){
        return timeRemaining;
    }
    public int getBuyNowPrice(){
        return buyNowPrice;
    }

}