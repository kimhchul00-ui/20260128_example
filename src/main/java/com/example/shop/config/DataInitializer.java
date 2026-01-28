package com.example.shop.config;

import com.example.shop.domain.cart.entity.Cart;
import com.example.shop.domain.cart.repository.CartRepository;
import com.example.shop.domain.delivery.entity.Delivery;
import com.example.shop.domain.member.entity.Member;
import com.example.shop.domain.member.repository.MemberRepository;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.entity.OrderItem;
import com.example.shop.domain.order.entity.Payment;
import com.example.shop.domain.order.repository.OrderRepository;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (memberRepository.count() > 0) {
            log.info("데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("샘플 데이터 초기화 시작...");

        // 회원 생성
        List<Member> members = createMembers();
        
        // 장바구니 생성
        createCarts(members);
        
        // 상품 생성
        List<Product> products = createProducts();
        
        // 샘플 주문 생성
        createOrders(members, products);

        log.info("샘플 데이터 초기화 완료!");
        log.info("===========================================");
        log.info("테스트 계정 정보:");
        log.info("관리자: admin@shop.com / admin123");
        log.info("사용자: user1@example.com / 1234");
        log.info("사용자: user2@example.com / 1234");
        log.info("사용자: user3@example.com / 1234");
        log.info("===========================================");
    }

    private List<Member> createMembers() {
        Member admin = Member.builder()
                .email("admin@shop.com")
                .password(passwordEncoder.encode("admin123"))
                .name("관리자")
                .phone("010-0000-0000")
                .address("서울시 강남구 테헤란로 123")
                .role(Member.Role.ROLE_ADMIN)
                .build();

        Member user1 = Member.builder()
                .email("user1@example.com")
                .password(passwordEncoder.encode("1234"))
                .name("홍길동")
                .phone("010-1111-1111")
                .address("서울시 강남구 역삼동 123-45")
                .role(Member.Role.ROLE_USER)
                .build();

        Member user2 = Member.builder()
                .email("user2@example.com")
                .password(passwordEncoder.encode("1234"))
                .name("김철수")
                .phone("010-2222-2222")
                .address("서울시 서초구 서초동 456-78")
                .role(Member.Role.ROLE_USER)
                .build();

        Member user3 = Member.builder()
                .email("user3@example.com")
                .password(passwordEncoder.encode("1234"))
                .name("이영희")
                .phone("010-3333-3333")
                .address("서울시 송파구 잠실동 789-12")
                .role(Member.Role.ROLE_USER)
                .build();

        return memberRepository.saveAll(Arrays.asList(admin, user1, user2, user3));
    }

    private void createCarts(List<Member> members) {
        for (Member member : members) {
            Cart cart = Cart.builder()
                    .member(member)
                    .build();
            cartRepository.save(cart);
        }
    }

    private List<Product> createProducts() {
        List<Product> products = Arrays.asList(
            // 의류
            Product.builder()
                    .name("베이직 면 티셔츠")
                    .description("편안한 착용감의 기본 면 티셔츠입니다. 다양한 색상으로 제공됩니다.")
                    .price(19900)
                    .stockQuantity(100)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/4A90D9/FFFFFF?text=T-Shirt")
                    .build(),
            Product.builder()
                    .name("슬림핏 청바지")
                    .description("세련된 슬림핏 디자인의 청바지입니다. 스트레치 소재로 편안합니다.")
                    .price(49900)
                    .stockQuantity(50)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/1E3A5F/FFFFFF?text=Jeans")
                    .build(),
            Product.builder()
                    .name("후드 집업 자켓")
                    .description("캐주얼한 스타일의 후드 집업 자켓입니다. 가볍고 따뜻합니다.")
                    .price(69900)
                    .stockQuantity(30)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/2E7D32/FFFFFF?text=Hoodie")
                    .build(),

            // 전자제품
            Product.builder()
                    .name("무선 블루투스 이어폰")
                    .description("고음질 무선 블루투스 이어폰입니다. 최대 20시간 재생 가능합니다.")
                    .price(89000)
                    .stockQuantity(200)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/9C27B0/FFFFFF?text=Earbuds")
                    .build(),
            Product.builder()
                    .name("스마트워치")
                    .description("다양한 건강 기능을 탑재한 스마트워치입니다. 심박수, 수면 패턴 측정 가능.")
                    .price(199000)
                    .stockQuantity(80)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/FF5722/FFFFFF?text=Watch")
                    .build(),
            Product.builder()
                    .name("휴대용 보조배터리")
                    .description("20000mAh 대용량 보조배터리입니다. 고속 충전을 지원합니다.")
                    .price(35000)
                    .stockQuantity(150)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/607D8B/FFFFFF?text=PowerBank")
                    .build(),

            // 식품
            Product.builder()
                    .name("유기농 견과류 세트")
                    .description("건강한 유기농 견과류 모음입니다. 아몬드, 호두, 캐슈넛 포함.")
                    .price(25000)
                    .stockQuantity(100)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/8D6E63/FFFFFF?text=Nuts")
                    .build(),
            Product.builder()
                    .name("프리미엄 꿀")
                    .description("100% 국내산 천연 벌꿀입니다. 500g 용량.")
                    .price(32000)
                    .stockQuantity(60)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/FFC107/FFFFFF?text=Honey")
                    .build(),
            Product.builder()
                    .name("수제 그래놀라")
                    .description("건강한 아침을 위한 수제 그래놀라입니다. 무설탕, 통곡물 함유.")
                    .price(18000)
                    .stockQuantity(120)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/795548/FFFFFF?text=Granola")
                    .build(),

            // 생활용품
            Product.builder()
                    .name("스테인리스 텀블러")
                    .description("보온보냉 기능의 스테인리스 텀블러입니다. 500ml 용량.")
                    .price(28000)
                    .stockQuantity(90)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/00BCD4/FFFFFF?text=Tumbler")
                    .build(),
            Product.builder()
                    .name("아로마 디퓨저")
                    .description("고급 아로마 오일 디퓨저입니다. LED 무드등 기능 포함.")
                    .price(45000)
                    .stockQuantity(40)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/E91E63/FFFFFF?text=Diffuser")
                    .build(),
            Product.builder()
                    .name("프리미엄 수건 세트")
                    .description("고급 면 100% 수건 세트입니다. 대형 2장, 소형 2장 구성.")
                    .price(35000)
                    .stockQuantity(70)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/03A9F4/FFFFFF?text=Towel")
                    .build()
        );

        return productRepository.saveAll(products);
    }

    private void createOrders(List<Member> members, List<Product> products) {
        // 주문 1: 배송 완료
        Member user1 = members.get(1);
        Product product1 = products.get(3); // 무선 블루투스 이어폰

        Order order1 = Order.builder()
                .member(user1)
                .status(Order.OrderStatus.DELIVERED)
                .totalAmount(product1.getPrice())
                .build();

        OrderItem orderItem1 = OrderItem.builder()
                .product(product1)
                .quantity(1)
                .price(product1.getPrice())
                .build();
        order1.addOrderItem(orderItem1);

        Payment payment1 = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.CREDIT_CARD)
                .amount(product1.getPrice())
                .status(Payment.PaymentStatus.COMPLETED)
                .build();
        payment1.complete();
        order1.setPayment(payment1);

        Delivery delivery1 = Delivery.builder()
                .receiverName(user1.getName())
                .receiverPhone(user1.getPhone())
                .address(user1.getAddress())
                .status(Delivery.DeliveryStatus.DELIVERED)
                .trackingNumber("1234567890")
                .build();
        delivery1.complete();
        order1.setDelivery(delivery1);

        orderRepository.save(order1);

        // 주문 2: 배송 중
        Member user2 = members.get(2);
        Product product2 = products.get(2); // 후드 집업 자켓

        Order order2 = Order.builder()
                .member(user2)
                .status(Order.OrderStatus.SHIPPING)
                .totalAmount(product2.getPrice())
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .product(product2)
                .quantity(1)
                .price(product2.getPrice())
                .build();
        order2.addOrderItem(orderItem2);

        Payment payment2 = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.KAKAO_PAY)
                .amount(product2.getPrice())
                .status(Payment.PaymentStatus.COMPLETED)
                .build();
        payment2.complete();
        order2.setPayment(payment2);

        Delivery delivery2 = Delivery.builder()
                .receiverName(user2.getName())
                .receiverPhone(user2.getPhone())
                .address(user2.getAddress())
                .status(Delivery.DeliveryStatus.SHIPPING)
                .trackingNumber("0987654321")
                .build();
        order2.setDelivery(delivery2);

        orderRepository.save(order2);

        // 주문 3: 결제 완료, 배송 대기
        Member user3 = members.get(3);
        Product product3a = products.get(0); // 베이직 면 티셔츠
        Product product3b = products.get(4); // 스마트워치

        int totalAmount = (product3a.getPrice() * 2) + product3b.getPrice();

        Order order3 = Order.builder()
                .member(user3)
                .status(Order.OrderStatus.PAID)
                .totalAmount(totalAmount)
                .build();

        OrderItem orderItem3a = OrderItem.builder()
                .product(product3a)
                .quantity(2)
                .price(product3a.getPrice())
                .build();
        order3.addOrderItem(orderItem3a);

        OrderItem orderItem3b = OrderItem.builder()
                .product(product3b)
                .quantity(1)
                .price(product3b.getPrice())
                .build();
        order3.addOrderItem(orderItem3b);

        Payment payment3 = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.BANK_TRANSFER)
                .amount(totalAmount)
                .status(Payment.PaymentStatus.COMPLETED)
                .build();
        payment3.complete();
        order3.setPayment(payment3);

        Delivery delivery3 = Delivery.builder()
                .receiverName(user3.getName())
                .receiverPhone(user3.getPhone())
                .address(user3.getAddress())
                .status(Delivery.DeliveryStatus.PENDING)
                .build();
        order3.setDelivery(delivery3);

        orderRepository.save(order3);
    }
}
