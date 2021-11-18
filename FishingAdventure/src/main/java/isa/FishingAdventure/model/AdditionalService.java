package isa.FishingAdventure.model;

public class AdditionalService {
   private int id;
   private String name;
   private double price;


   public AdditionalService(int id, String name, double price) {
      this.id = id;
      this.name = name;
      this.price = price;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public double getPrice() {
      return this.price;
   }

   public void setPrice(double price) {
      this.price = price;
   }

}