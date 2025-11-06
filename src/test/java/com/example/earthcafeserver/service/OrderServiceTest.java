package com.example.earthcafeserver.service;

import com.example.earthcafeserver.domain.member.Gender;
import com.example.earthcafeserver.domain.member.Member;
import com.example.earthcafeserver.domain.member.Role;
import com.example.earthcafeserver.domain.order.Order;
import com.example.earthcafeserver.domain.order.OrderStatus;
import com.example.earthcafeserver.domain.product.Product;
import com.example.earthcafeserver.domain.product.ProductOption;
import com.example.earthcafeserver.dto.order.OrderItemRequest;
import com.example.earthcafeserver.dto.order.OrderRequest;
import com.example.earthcafeserver.dto.order.OrderResponse;
import com.example.earthcafeserver.dto.order.OrderSummaryResponse;
import com.example.earthcafeserver.repository.MemberRepository;
import com.example.earthcafeserver.repository.OrderRepository;
import com.example.earthcafeserver.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Member createMember() {
        return new Member("홍길동", "010-1234-5678", Gender.MALE, Role.BASIC_USER, LocalDate.of(1990, 1, 1));
    }

    private Product createProduct() {
        Product product = new Product("아메리카노", 4000L);
        product.addOption(new ProductOption(product, "HOT", 0L));
        product.addOption(new ProductOption(product, "ICE", 0L));
        product.addOption(new ProductOption(product, "샷추가 ", 500L));
        return product;
    }

    @Test
    void insertOrder_success() {
        // given
        Member member = memberRepository.save(createMember());
        Product product = productRepository.save(createProduct());

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(product.getId());
        item.setQuantity(2);
        item.setOptions(List.of(1L));

        OrderRequest request = new OrderRequest();
        request.setMemberId(member.getId());
        request.setOrderItems(List.of(item));

        // when
        OrderResponse response = orderService.insertOrder(request);

        // then
        assertEquals(member.getId(), response.getMemberId());
        assertEquals(8000L, response.getTotalAmount());
    }

    @Test
    void getOrderById_success() {
        // given
        Member member = memberRepository.save(createMember());
        Product product = productRepository.save(createProduct());

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(product.getId());
        item.setQuantity(2);
        item.setOptions(List.of(1L));

        OrderRequest request = new OrderRequest();
        request.setMemberId(member.getId());
        request.setOrderItems(List.of(item));

        OrderResponse response = orderService.insertOrder(request);

        // when
        Long orderId = response.getOrderId();
        OrderResponse orderById = orderService.getOrderById(orderId);

        // then
        assertEquals(response.getOrderId(), orderById.getOrderId());
        assertEquals(response.getMemberId(), orderById.getMemberId());
        assertEquals(response.getTotalAmount(), orderById.getTotalAmount());
    }

    @Test
    void cancelOrder_success() {
        // given
        Member member = memberRepository.save(createMember());
        Product product = productRepository.save(createProduct());

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(product.getId());
        item.setQuantity(2);
        item.setOptions(List.of(1L));

        OrderRequest request = new OrderRequest();
        request.setMemberId(member.getId());
        request.setOrderItems(List.of(item));

        OrderResponse response = orderService.insertOrder(request);

        Order order = orderRepository.findById(response.getOrderId()).orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
        order.changeStatus(OrderStatus.CONFIRMED);

        // when
        orderService.cancelOrder(response.getOrderId(), "SYSTEM");

        //then
        OrderResponse orderById = orderService.getOrderById(response.getOrderId());
        assertEquals("CANCELED", orderById.getOrderStatus());
    }

    @Test
    void cancelOrder_fail() {
        // given
        Member member = memberRepository.save(createMember());
        Product product = productRepository.save(createProduct());

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(product.getId());
        item.setQuantity(2);
        item.setOptions(List.of(1L));

        OrderRequest request = new OrderRequest();
        request.setMemberId(member.getId());
        request.setOrderItems(List.of(item));

        OrderResponse response = orderService.insertOrder(request);

        // then
        assertThrows(IllegalStateException.class, () ->
                orderService.cancelOrder(response.getOrderId(), "SYSTEM"));
    }

    @Test
    void getOrdersByMember_success() {
        // given
        Member member = memberRepository.save(createMember());
        Product product1 = productRepository.save(createProduct());
        Product product2 = productRepository.save(new Product("카라멜마끼야또", 8000L));

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(product1.getId());
        item1.setQuantity(2);
        item1.setOptions(List.of(1L));

        OrderRequest request1 = new OrderRequest();
        request1.setMemberId(member.getId());
        request1.setOrderItems(List.of(item1));

        OrderResponse response1 = orderService.insertOrder(request1);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setProductId(product2.getId());
        item2.setQuantity(3);

        OrderRequest request2 = new OrderRequest();
        request2.setMemberId(member.getId());
        request2.setOrderItems(List.of(item2));

        OrderResponse response2 = orderService.insertOrder(request2);

        // when
        List<OrderSummaryResponse> ordersByMember = orderService.getOrdersByMember(member.getId());

        // then
        assertEquals(2, ordersByMember.size());
    }
}