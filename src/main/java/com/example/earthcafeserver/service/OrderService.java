package com.example.earthcafeserver.service;

import com.example.earthcafeserver.domain.member.Member;
import com.example.earthcafeserver.domain.order.*;
import com.example.earthcafeserver.domain.product.Product;
import com.example.earthcafeserver.domain.product.ProductOption;
import com.example.earthcafeserver.dto.order.*;
import com.example.earthcafeserver.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderItemOptionRepository orderItemOptionRepository;

    private final OrderHistoryRepository orderHistoryRepository;

    private final MemberRepository memberRepository;

    private final ProductRepository productRepository;

    private final ProductOptionRepository productOptionRepository;

    public OrderResponse insertOrder(OrderRequest orderRequest) {
        // 1) 회원 확인
        Long memberId = orderRequest.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        // 2) 주문 생성
        Order save = orderRepository.save(new Order(member));

        // 3) 이력 남기기
        // TODO changeBy 추가하기
        orderHistoryRepository.save(
                new OrderHistory(save, null, OrderStatus.CREATED, "SYSTEM")
        );

        // 4) 주문 상품 생성
        for (OrderItemRequest request : orderRequest.getOrderItems()) {
            Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

            // 기본 단가
            OrderItem orderItem = new OrderItem(save, product, request.getQuantity(), product.getPrice());

            // 일단 주문에 붙이기 (양방향 정리 (order <-> orderItem)
            save.addItem(orderItem);
            orderItemRepository.save(orderItem);

            // 5) 옵션 처리
            if (request.getOptions() != null && !request.getOptions().isEmpty()) {
                for (Long orderItemOptionId : request.getOptions()) {
                    ProductOption productOption = productOptionRepository.findByProductIdAndProductOptionId(product.getId(), orderItemOptionId).orElseThrow(() -> new IllegalArgumentException("해당 옵션을 찾을 수 없습니다."));

                    // 이 옵션이 해당 상품에 속하는지 검증
                    if (!productOption.getProduct().getId().equals(product.getId())) {
                        throw new IllegalArgumentException("상품에 속하지 않은 옵션입니다. productId=" +
                                product.getId() + ", optionId=" + orderItemOptionId);
                    }

                    // 주문 시점의 옵션 금액 복사
                    OrderItemOption orderItemOption = new OrderItemOption(productOption, productOption.getExtraPrice());
                    orderItemOption.setOrderItem(orderItem);

                    // 양방향 정리 (orderItem <-> orderItemOption)
                    orderItem.addOption(orderItemOption);
                    orderItemOptionRepository.save(orderItemOption);

                    // 옵션 금액 포함한 주문 금액 정리
                    orderItem.setLineAmount(orderItem.getLineAmount() + productOption.getExtraPrice());
                }
            }
        }

        // 6) 전체 주문 금액 다시 계싼
        save.recalcTotal();

        return OrderResponse.from(save);
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, String changedBy) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("주문 취소는 성공한 결제에서만 가능합니다.");
        }

        orderHistoryRepository.save(
                new OrderHistory(order, order.getStatus(), OrderStatus.CANCELED, "SYSTEM")
        );
        order.changeStatus(OrderStatus.CANCELED);
    }

    public List<OrderSummaryResponse> getOrdersByMember(Long memberId) {
        List<Order> members = orderRepository.findByMemberId(memberId);

        return members.stream().map(OrderSummaryResponse::from).toList();
    }

    public List<OrderHistoryResponse> getOrderHistory(Long orderId) {
        List<OrderHistory> historyList = orderHistoryRepository.findByOrderId(orderId);
        return historyList.stream().map(OrderHistoryResponse::from).toList();
    }

    public List<OrderItemResponse> getOrderItems(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return items.stream().map(OrderItemResponse::from).toList();
    }
}
