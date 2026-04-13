package com.kaushik.travelplan.dto;

public class GroupCostBreakdown {
    private int travelers;
    private int roomsNeeded;
    private int hotelCostTotal;     // rooms * pricePerNight * days
    private int hotelCostPerPerson;
    private int foodCostTotal;      // foodPerDay * travelers * days
    private int foodCostPerPerson;
    private int transportCostTotal; // shared transport
    private int transportCostPerPerson;
    private int placesCostTotal;    // entryFee * travelers
    private int placesCostPerPerson;
    private int totalCost;
    private int perPersonCost;
    private int savingsVsSolo;      // how much cheaper per person vs solo trip
    private String savingsMessage;

    public int getTravelers() { return travelers; }
    public void setTravelers(int travelers) { this.travelers = travelers; }

    public int getRoomsNeeded() { return roomsNeeded; }
    public void setRoomsNeeded(int roomsNeeded) { this.roomsNeeded = roomsNeeded; }

    public int getHotelCostTotal() { return hotelCostTotal; }
    public void setHotelCostTotal(int hotelCostTotal) { this.hotelCostTotal = hotelCostTotal; }

    public int getHotelCostPerPerson() { return hotelCostPerPerson; }
    public void setHotelCostPerPerson(int hotelCostPerPerson) { this.hotelCostPerPerson = hotelCostPerPerson; }

    public int getFoodCostTotal() { return foodCostTotal; }
    public void setFoodCostTotal(int foodCostTotal) { this.foodCostTotal = foodCostTotal; }

    public int getFoodCostPerPerson() { return foodCostPerPerson; }
    public void setFoodCostPerPerson(int foodCostPerPerson) { this.foodCostPerPerson = foodCostPerPerson; }

    public int getTransportCostTotal() { return transportCostTotal; }
    public void setTransportCostTotal(int transportCostTotal) { this.transportCostTotal = transportCostTotal; }

    public int getTransportCostPerPerson() { return transportCostPerPerson; }
    public void setTransportCostPerPerson(int transportCostPerPerson) { this.transportCostPerPerson = transportCostPerPerson; }

    public int getPlacesCostTotal() { return placesCostTotal; }
    public void setPlacesCostTotal(int placesCostTotal) { this.placesCostTotal = placesCostTotal; }

    public int getPlacesCostPerPerson() { return placesCostPerPerson; }
    public void setPlacesCostPerPerson(int placesCostPerPerson) { this.placesCostPerPerson = placesCostPerPerson; }

    public int getTotalCost() { return totalCost; }
    public void setTotalCost(int totalCost) { this.totalCost = totalCost; }

    public int getPerPersonCost() { return perPersonCost; }
    public void setPerPersonCost(int perPersonCost) { this.perPersonCost = perPersonCost; }

    public int getSavingsVsSolo() { return savingsVsSolo; }
    public void setSavingsVsSolo(int savingsVsSolo) { this.savingsVsSolo = savingsVsSolo; }

    public String getSavingsMessage() { return savingsMessage; }
    public void setSavingsMessage(String savingsMessage) { this.savingsMessage = savingsMessage; }
}
