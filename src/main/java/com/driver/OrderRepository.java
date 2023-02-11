package com.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.Optional;

public class OrderRepository {

    Map<String,Order>orderMap;
    Map<String,DeliveryPartner>partnerMap;
    Map<String, List<String>>OPMap;
    Set<String> orderNotAssigned;

    public OrderRepository() {
        this.orderMap = new HashMap<>();
        this.partnerMap=new HashMap<>();
        this.OPMap=new HashMap<>();
        this.orderNotAssigned =  new HashSet<>();
    }
    //Add an Order: POST /orders/add-order
    // Pass the Order object as request body
    // Return success message wrapped in a ResponseEntity object
    // Controller Name - addOrder

    public void addOrder(Order order){
        orderMap.put(order.getId(),order);
        orderNotAssigned.add(order.getId());

    }
   // Add a Delivery Partner: POST /orders/add-partner/{partnerId}
    // Pass the partnerId string as path variable
    // Return success message wrapped in a ResponseEntity object
    // Controller Name - addPartner
   public void addPartner(String partnerId){
        partnerMap.put(partnerId, new DeliveryPartner(partnerId));

   }
    //Assign an order to a partner: PUT /orders/add-order-partner-pair//
    // Pass orderId and partnerId strings as request parameters
    // Return success message wrapped in a ResponseEntity object//
    // Controller Name - addOrderPartnerPair
    public void addOrderPartnerPair(String orderId,String partnerId){
        partnerMap.get(partnerId).setNumberOfOrders(partnerMap.get(partnerId).getNumberOfOrders()+1);
        if(OPMap.containsKey(partnerId)){
            List<String> orderList = OPMap.get(partnerId);
            orderList.add(orderId);
            orderNotAssigned.remove(orderId);
            return;
        }

        OPMap.put(partnerId,new ArrayList<>(Arrays.asList(orderId)));
        orderNotAssigned.remove(orderId);
    }
    public Order getOrderById(String orderId){
        return orderMap.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return partnerMap.get(partnerId);
    }
    public int getOrderCountByPartnerId(String partnerId){
        return OPMap.get(partnerId).size();
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orderList = new ArrayList<>(); //This list has to be returned after filling

        //Let's first fetch the list (of Strings) of all the orderIds from the partnerOrderMap database
        List<String> orderIdList = OPMap.get(partnerId);
        for(String order : orderIdList){
            orderList.add(orderMap.get(order).getId());
        }
        return orderList;
    }
    public List<String> getAllOrders(){
        //Lets fetch all the values from the orderDatabase
        Collection<Order> values = orderMap.values();

        //Now fill all these values in a list and return it
        List<String> orderList = new ArrayList<>();
        for(Order o : values){
            orderList.add(o.getId());
        }
        return orderList;
    }
    public int getCountOfUnassignedOrders(){
        return orderNotAssigned.size();
    }
    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int numericalTime = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3,5));
        int count = 0;
        for(String orderId : OPMap.get(partnerId)){
            if(orderMap.get(orderId).getDeliveryTime()>numericalTime){
                count++;
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int latestTime = 0;
        if(OPMap.containsKey(partnerId)){
            for(String currOrderId : OPMap.get(partnerId)){
                if(orderMap.get(currOrderId).getDeliveryTime()>latestTime){
                    latestTime = orderMap.get(currOrderId).getDeliveryTime();
                }
            }
        }

        int hours = latestTime/60;
        int minute = latestTime%60;

        String strhours = Integer.toString(hours);
        if(strhours.length()==1){
            strhours = "0"+strhours;
        }

        String minutes = Integer.toString(minute);
        if(minutes.length()==1){
            minutes = "0" + minutes;
        }
        return strhours + ":" + minutes;

    }
    public void deletePartnerById(String partnerId){
        if(!OPMap.isEmpty()){
            orderNotAssigned.addAll(OPMap.get(partnerId));
        }
        OPMap.remove(partnerId);
        OPMap.remove(partnerId);
    }
    public void deleteOrderById(String orderId){
        orderMap.remove(orderId);
        if(orderNotAssigned.contains(orderId)){
            orderNotAssigned.remove(orderId);
        }
        else {
            for(List<String> listofOrderIds : OPMap.values()){
                listofOrderIds.remove(orderId);
            }

        }
    }



}
