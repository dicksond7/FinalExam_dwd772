package ClientSide;

import java.io.Serializable;

public class AuctionItem implements Serializable {
    private String name;
    private int bid;
    private int price;
    private boolean isSold;

    AuctionItem(String name, int startingPrice){
        this.name = name;
        this.price = startingPrice;
        this.isSold = false;
        this.bid = 0;
    }

    public void setPrice(int bid){
        this.price = price += bid;
    }

    public int getPrice(){
        return price;
    }

    public void sold(){
        this.isSold = true;
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


}
