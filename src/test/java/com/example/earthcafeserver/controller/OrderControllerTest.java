package com.example.earthcafeserver.controller;

import com.example.earthcafeserver.domain.member.Gender;
import com.example.earthcafeserver.domain.member.Member;
import com.example.earthcafeserver.domain.member.MemberStatus;
import com.example.earthcafeserver.domain.member.Role;
import com.example.earthcafeserver.domain.product.Product;
import com.example.earthcafeserver.domain.product.ProductOption;
import com.example.earthcafeserver.repository.MemberRepository;
import com.example.earthcafeserver.repository.ProductOptionRepository;
import com.example.earthcafeserver.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Test
    @DisplayName("주문 생성을 성공한다.")
    void createOrder_success() throws Exception {
        // 1) 회원 세팅
        Member member = new Member(
                "홍길동",
                "010-1234-5678",
                Gender.MALE,
                Role.BASIC_USER,
                LocalDate.of(1990, 1, 1)
        );
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);

        // 2) 상품 + 옵션 세팅
        Product product = new Product("아메리카노", 4000L);
        product.setIsActive(true);
        productRepository.save(product);

        ProductOption hot = new ProductOption(product, "HOT", 0L);
        productOptionRepository.save(hot);

        // 3) 주문 요청 Request 생성
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("memberId", member.getId());

        ObjectNode orderItemNode = mapper.createObjectNode();
        orderItemNode.put("productId", product.getId());
        orderItemNode.put("quantity", 2);
        orderItemNode.putArray("options").add(hot.getId());

        objectNode.putArray("orderItems").add(orderItemNode);

        // 4) 주문 생성 호출
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectNode.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andExpect(jsonPath("$.orderItems[0].productName").value("아메리카노"))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("주문 취소한다.")
    void cancelOrder_success() throws Exception {
        // 1) 회원 세팅
        Member member = new Member(
                "홍길동",
                "010-1234-5678",
                Gender.MALE,
                Role.BASIC_USER,
                LocalDate.of(1990, 1, 1)
        );
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);

        // 2) 상품 + 옵션 세팅
        Product product = new Product("아메리카노", 4000L);
        product.setIsActive(true);
        productRepository.save(product);

        ProductOption hot = new ProductOption(product, "HOT", 0L);
        productOptionRepository.save(hot);

        // 3) 주문 요청 Request 생성
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("memberId", member.getId());

        ObjectNode orderItemNode = mapper.createObjectNode();
        orderItemNode.put("productId", product.getId());
        orderItemNode.put("quantity", 2);
        orderItemNode.putArray("options").add(hot.getId());

        objectNode.putArray("orderItems").add(orderItemNode);

        // 4) 주문 생성 호출
        String orderResponseJson = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectNode.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode orderResponse = mapper.readTree(orderResponseJson);
        Long orderId = orderResponse.get("orderId").asLong();

        // 5) 결제 요청 Request 생성
        ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.put("orderId", orderId);
        objectNode1.put("amount", 8000L);
        objectNode1.put("idempotencyKey", "test-123");

        // 6) 결제 요청
        String payResponseJson = mockMvc.perform(post("/api/orders/{orderId}/payments", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectNode1.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode payResponse = mapper.readTree(payResponseJson);
        assertThat(payResponse.get("paymentId").asLong()).isPositive();

        // 7) 결제 후 주문 상태가 변경되었는지 조회로 검증
        String orderAfterPayJson = mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode orderAfterPay = mapper.readTree(orderAfterPayJson);
        Long orderId1 = orderAfterPay.get("orderId").asLong();


        // 5) 주문 취소 호출
        mockMvc.perform(patch("/api/orders/" + orderId + "/cancel"))
                .andExpect(status().isBadRequest()); //결제 취소 전이기 때문에 400
    }

    @Test
    @DisplayName("주문 생성 후 결제까지 성공한다")
    void createOrder_and_pay_success() throws Exception {
        // 1) 회원 세팅
        Member member = new Member(
                "홍길동",
                "010-1234-5678",
                Gender.MALE,
                Role.BASIC_USER,
                LocalDate.of(1990, 1, 1)
        );
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);

        // 2) 상품 + 옵션 세팅
        Product product = new Product("아메리카노", 4000L);
        product.setIsActive(true);
        productRepository.save(product);

        ProductOption hot = new ProductOption(product, "HOT", 0L);
        productOptionRepository.save(hot);

        // 3) 주문 요청 Request 생성
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("memberId", member.getId());

        ObjectNode orderItemNode = mapper.createObjectNode();
        orderItemNode.put("productId", product.getId());
        orderItemNode.put("quantity", 2);
        orderItemNode.putArray("options").add(hot.getId());

        objectNode.putArray("orderItems").add(orderItemNode);

        // 4) 주문 생성 호출
        String orderResponseJson = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectNode.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode orderResponse = mapper.readTree(orderResponseJson);
        Long orderId = orderResponse.get("orderId").asLong();

        // 5) 결제 요청 Request 생성
        ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.put("orderId", orderId);
        objectNode1.put("amount", 8000L);
        objectNode1.put("idempotencyKey", "test-123");

        // 6) 결제 요청
        String payResponseJson = mockMvc.perform(post("/api/orders/{orderId}/payments", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectNode1.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode payResponse = mapper.readTree(payResponseJson);
        assertThat(payResponse.get("paymentId").asLong()).isPositive();

        // 7) 결제 후 주문 상태가 변경되었는지 조회로 검증
        String orderAfterPayJson = mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode orderAfterPay = mapper.readTree(orderAfterPayJson);
        assertThat(orderAfterPay.get("orderStatus").asText()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("같은 주문에 같은 멱등키로 두 번 결제 요청하면 두 번째는 새로 만들지 않고 이전 결제 그대로")
    void pay_twice_with_same_idempotencyKey() throws Exception {
        // 회원/상품/주문 생성 부분은 위 테스트와 동일하게 재사용
        Member member = new Member(
                "성춘향",
                "010-2222-3333",
                Gender.FEMALE,
                Role.BASIC_USER,
                LocalDate.of(1992, 3, 10)
        );
        memberRepository.save(member);

        Product product = new Product("카페라떼", 5500L);
        product.setIsActive(true);
        productRepository.save(product);

        ProductOption ice = new ProductOption(product, "ICE", 0L);
        productOptionRepository.save(ice);

        // 주문 생성
        ObjectNode orderReq = mapper.createObjectNode();
        orderReq.put("memberId", member.getId());
        ObjectNode item = mapper.createObjectNode();
        item.put("productId", product.getId());
        item.put("quantity", 1);
        item.putArray("options").add(ice.getId());
        orderReq.putArray("orderItems").add(item);

        String orderJson = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderReq.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long orderId = mapper.readTree(orderJson).get("orderId").asLong();

        // 결제 요청 1
        ObjectNode payReq = mapper.createObjectNode();
        payReq.put("orderId", orderId);
        payReq.put("amount", 5500L);
        payReq.put("idempotencyKey", "idem-001");

        String firstPayJson = mockMvc.perform(post("/api/orders/{orderId}/payments", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payReq.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long firstPaymentId = mapper.readTree(firstPayJson).get("paymentId").asLong();

        // 결제 요청 2 (같은 키)
        String secondPayJson = mockMvc.perform(post("/api/orders/{orderId}/payments", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payReq.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long secondPaymentId = mapper.readTree(secondPayJson).get("paymentId").asLong();

        // 같은 결제여야 한다
        assertThat(secondPaymentId).isEqualTo(firstPaymentId);
    }
}