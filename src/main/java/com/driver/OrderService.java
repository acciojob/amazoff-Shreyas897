package com.driver;

public class OrderService {
    OrderRepository orderRepository=new OrderRepository();
    public void addOrder(Order order){
        orderRepository.addOrder(order);
    }
    public void addPartner(String partnerId){
        orderRepository.addPartner(partnerId);
    }
}
