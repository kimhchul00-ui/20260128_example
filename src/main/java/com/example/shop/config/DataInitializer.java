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
            Product.builder()
                    .name("니트 카디건")
                    .description("부드러운 니트 소재의 카디건입니다. 봄가을 외출에 좋습니다.")
                    .price(59000)
                    .stockQuantity(45)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/5D4037/FFFFFF?text=Cardigan")
                    .build(),
            Product.builder()
                    .name("코튼 트렌치코트")
                    .description("클래식한 코튼 트렌치코트입니다. 방풍 방수 기능.")
                    .price(129000)
                    .stockQuantity(25)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/455A64/FFFFFF?text=Trench")
                    .build(),
            Product.builder()
                    .name("플리스 배낭")
                    .description("가벼운 플리스 재질의 캐주얼 배낭입니다.")
                    .price(39000)
                    .stockQuantity(60)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/00695C/FFFFFF?text=Backpack")
                    .build(),
            Product.builder()
                    .name("레귤러 핏 반바지")
                    .description("시원한 여름용 레귤러 핏 반바지입니다.")
                    .price(29900)
                    .stockQuantity(80)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/37474F/FFFFFF?text=Shorts")
                    .build(),
            Product.builder()
                    .name("오버사이즈 맨투맨")
                    .description("편안한 오버사이즈 맨투맨입니다. 무지 3색.")
                    .price(45000)
                    .stockQuantity(55)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/4E342E/FFFFFF?text=Sweatshirt")
                    .build(),
            Product.builder()
                    .name("울 블렌드 코트")
                    .description("따뜻한 울 블렌드 겨울 코트입니다.")
                    .price(189000)
                    .stockQuantity(20)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/263238/FFFFFF?text=Coat")
                    .build(),
            Product.builder()
                    .name("린넨 셔츠")
                    .description("시원한 린넨 셔츠입니다. 비즈니스 캐주얼에 적합.")
                    .price(55000)
                    .stockQuantity(40)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/78909C/FFFFFF?text=Linen")
                    .build(),
            Product.builder()
                    .name("스키니 슬랙스")
                    .description("드레시한 스키니 슬랙스입니다. 정장 하의로 추천.")
                    .price(65000)
                    .stockQuantity(35)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300/546E7A/FFFFFF?text=Slacks")
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
            Product.builder()
                    .name("USB-C 멀티 허브")
                    .description("7in1 USB-C 멀티 허브. HDMI, USB3.0, SD카드 슬롯.")
                    .price(45000)
                    .stockQuantity(120)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/7B1FA2/FFFFFF?text=Hub")
                    .build(),
            Product.builder()
                    .name("블루투스 스피커")
                    .description("휴대용 블루투스 스피커. 20시간 재생, 방수.")
                    .price(65000)
                    .stockQuantity(90)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/E64A19/FFFFFF?text=Speaker")
                    .build(),
            Product.builder()
                    .name("무선 충전 패드")
                    .description("QI 15W 고속 무선 충전 패드. 스탠드형.")
                    .price(35000)
                    .stockQuantity(130)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/00838F/FFFFFF?text=Charger")
                    .build(),
            Product.builder()
                    .name("키보드 마우스 세트")
                    .description("무선 키보드 마우스 세트. 저소음, 한국어 배열.")
                    .price(55000)
                    .stockQuantity(85)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/5D4037/FFFFFF?text=KB")
                    .build(),
            Product.builder()
                    .name("웹캠 HD")
                    .description("1080p HD 웹캠. 자동 초점, 내장 마이크.")
                    .price(79000)
                    .stockQuantity(70)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/455A64/FFFFFF?text=Webcam")
                    .build(),
            Product.builder()
                    .name("이어폰 케이스")
                    .description("무선 이어폰용 실리콘 케이스. 다양한 컬러.")
                    .price(12000)
                    .stockQuantity(200)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/78909C/FFFFFF?text=Case")
                    .build(),
            Product.builder()
                    .name("노트북 스탠드")
                    .description("알루미늄 노트북 스탠드. 각도 조절, 쿨링.")
                    .price(42000)
                    .stockQuantity(65)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/37474F/FFFFFF?text=Stand")
                    .build(),
            Product.builder()
                    .name("HDMI 케이블 2m")
                    .description("고속 HDMI 2.1 케이블 2m. 4K 120Hz 지원.")
                    .price(18000)
                    .stockQuantity(180)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/546E7A/FFFFFF?text=HDMI")
                    .build(),
            Product.builder()
                    .name("USB 메모리 64GB")
                    .description("USB 3.2 64GB 플래시 드라이브. 슬림형.")
                    .price(22000)
                    .stockQuantity(160)
                    .category("전자제품")
                    .imageUrl("https://via.placeholder.com/300x300/263238/FFFFFF?text=USB")
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
            Product.builder()
                    .name("콜드브루 원두")
                    .description("에티오피아 싱글 오리진. 200g whole bean.")
                    .price(18000)
                    .stockQuantity(95)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/3E2723/FFFFFF?text=Coffee")
                    .build(),
            Product.builder()
                    .name("올리브 오일")
                    .description("엑스트라 버진 올리브 오일 500ml. 지중해산.")
                    .price(22000)
                    .stockQuantity(75)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/558B2F/FFFFFF?text=Olive")
                    .build(),
            Product.builder()
                    .name("다크 초콜릿")
                    .description("72% 카카오 다크 초콜릿 100g. 벨기에산.")
                    .price(12000)
                    .stockQuantity(110)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/4E342E/FFFFFF?text=Choco")
                    .build(),
            Product.builder()
                    .name("견과류 믹스")
                    .description("로스팅 견과류 믹스 400g. 아몬드, 캐슈, 마카다미아.")
                    .price(15000)
                    .stockQuantity(88)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/6D4C41/FFFFFF?text=NutMix")
                    .build(),
            Product.builder()
                    .name("건조 망고")
                    .description("태국산 건조 망고 200g. 무설탕.")
                    .price(14000)
                    .stockQuantity(70)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/FF8F00/FFFFFF?text=Mango")
                    .build(),
            Product.builder()
                    .name("그래놀라 바")
                    .description("오트밀 그래놀라 바 6입. 아침 대용.")
                    .price(9000)
                    .stockQuantity(140)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/D84315/FFFFFF?text=Bar")
                    .build(),
            Product.builder()
                    .name("유기농 현미")
                    .description("국내산 유기농 현미 2kg.")
                    .price(12000)
                    .stockQuantity(100)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/8D6E63/FFFFFF?text=Rice")
                    .build(),
            Product.builder()
                    .name("생강 차")
                    .description("건생강 슬라이스 차 50g. 티백 20입.")
                    .price(11000)
                    .stockQuantity(85)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/FF7043/FFFFFF?text=Ginger")
                    .build(),
            Product.builder()
                    .name("훈제 연어")
                    .description("노르웨이산 훈제 연어 200g. 냉장.")
                    .price(28000)
                    .stockQuantity(45)
                    .category("식품")
                    .imageUrl("https://via.placeholder.com/300x300/EF5350/FFFFFF?text=Salmon")
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
                    .build(),
            Product.builder()
                    .name("실리콘 매트")
                    .description("주방용 실리콘 베이킹 매트. 내열 230도.")
                    .price(15000)
                    .stockQuantity(95)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/26A69A/FFFFFF?text=Mat")
                    .build(),
            Product.builder()
                    .name("유리 보관병")
                    .description("무균 유리 보관병 500ml 4개 세트. 밀봉.")
                    .price(22000)
                    .stockQuantity(60)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/80CBC4/FFFFFF?text=Jar")
                    .build(),
            Product.builder()
                    .name("LED 책상 조명")
                    .description("USB LED 스탠드. 3단 밝기, 충전포트.")
                    .price(35000)
                    .stockQuantity(55)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/FFB74D/FFFFFF?text=Lamp")
                    .build(),
            Product.builder()
                    .name("스탠드 화분")
                    .description("세라믹 스탠드 화분 소형. 드레인홀.")
                    .price(18000)
                    .stockQuantity(50)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/81C784/FFFFFF?text=Pot")
                    .build(),
            Product.builder()
                    .name("욕실 수납선반")
                    .description("스테인리스 욕실 수납선반 2단. 접착식.")
                    .price(12000)
                    .stockQuantity(80)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/90A4AE/FFFFFF?text=Shelf")
                    .build(),
            Product.builder()
                    .name("슬리퍼 홀더")
                    .description("욕실용 슬리퍼 거치대. 물빠짐 양호.")
                    .price(8000)
                    .stockQuantity(120)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/B0BEC5/FFFFFF?text=Holder")
                    .build(),
            Product.builder()
                    .name("빨래 널이")
                    .description("이동식 빨래 널이. 접이식, 실내용.")
                    .price(25000)
                    .stockQuantity(40)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/64B5F6/FFFFFF?text=Dry")
                    .build(),
            Product.builder()
                    .name("화장지 30롤")
                    .description("3겹 순수 펄프 화장지 30롤. 대용량.")
                    .price(18000)
                    .stockQuantity(150)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/FFF59D/FFFFFF?text=TP")
                    .build(),
            Product.builder()
                    .name("캔들 워머")
                    .description("LED 캔들 워머. 무선, 3단 타이머.")
                    .price(32000)
                    .stockQuantity(45)
                    .category("생활용품")
                    .imageUrl("https://via.placeholder.com/300x300/F48FB1/FFFFFF?text=Candle")
                    .build()
        );

        return productRepository.saveAll(products);
    }

    private void createOrders(List<Member> members, List<Product> products) {
        // 주문 1: 배송 완료
        Member user1 = members.get(1);
        Product product1 = products.get(10); // 무선 블루투스 이어폰 (전자제품 첫 번째)

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
        Product product3b = products.get(11); // 스마트워치 (전자제품 두 번째)

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
