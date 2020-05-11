package final_exam;

import java.io.Serializable;

public class AuctionItem implements Serializable {
    private String name;
    private double bid;
    private double price;
    private boolean isSold;
    private int pagePosition; // position on each clients grid map
    private final double buyNowPrice;
    private String buyer;
    private String recentHistory;


    AuctionItem(String name, double startingPrice, int pagePosition, double buyNowPrice){
        this.name = name;
        this.price = startingPrice;
        this.pagePosition = pagePosition;
        this.isSold = false;
        this.buyNowPrice = buyNowPrice;
        this.bid = 0;
        this.recentHistory = name + " auction started.";

    }


    public void setPrice(double bid, String buyer){
        this.price = bid;
        this.buyer = buyer;
        //this.bid = 0;
    }

    public double getPrice(){
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

    public void setBid(double bid){
        this.bid = bid;
    }

    public double getBid(){
        return this.bid;
    }

    public void setPagePosition(int position){
        this.pagePosition = position;
    }
    public int getPagePosition(){
        return this.pagePosition;
    }

    public double getBuyNowPrice(){
        return buyNowPrice;
    }

    public String getBuyer(){
        return buyer;
    }

    public void setHistory(String history){
        this.recentHistory = history;
    }
    public String getRecentHistory(){
        return recentHistory;
    }

}