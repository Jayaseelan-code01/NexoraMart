package com.jayaseelan.nexoramart.model;
import java.math.BigDecimal;
public class Product {
  private long id,sellerId; private String sellerName,name,brand,model,description,category,ram,storage,displaySize,camera,battery,imageUrl1,imageUrl2,imageUrl3; private BigDecimal price; private int stockQty;
  public long getId(){return id;} public void setId(long v){id=v;}
  public long getSellerId(){return sellerId;} public void setSellerId(long v){sellerId=v;}
  public String getSellerName(){return sellerName;} public void setSellerName(String v){sellerName=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public String getBrand(){return brand;} public void setBrand(String v){brand=v;}
  public String getModel(){return model;} public void setModel(String v){model=v;}
  public String getDescription(){return description;} public void setDescription(String v){description=v;}
  public String getCategory(){return category;} public void setCategory(String v){category=v;}
  public String getRam(){return ram;} public void setRam(String v){ram=v;}
  public String getStorage(){return storage;} public void setStorage(String v){storage=v;}
  public String getDisplaySize(){return displaySize;} public void setDisplaySize(String v){displaySize=v;}
  public String getCamera(){return camera;} public void setCamera(String v){camera=v;}
  public String getBattery(){return battery;} public void setBattery(String v){battery=v;}
  public String getImageUrl1(){return imageUrl1;} public void setImageUrl1(String v){imageUrl1=v;}
  public String getImageUrl2(){return imageUrl2;} public void setImageUrl2(String v){imageUrl2=v;}
  public String getImageUrl3(){return imageUrl3;} public void setImageUrl3(String v){imageUrl3=v;}
  public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
  public int getStockQty(){return stockQty;} public void setStockQty(int v){stockQty=v;}
}
