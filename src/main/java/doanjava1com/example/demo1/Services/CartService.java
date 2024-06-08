package doanjava1com.example.demo1.Services;

import com.sun.istack.NotNull;
import doanjava1com.example.demo1.Models.Invoice;
import doanjava1com.example.demo1.Models.ItemInvoice;
import doanjava1com.example.demo1.Models.User;
import doanjava1com.example.demo1.Repositories.ClothRepository;
import doanjava1com.example.demo1.Repositories.IInvoiceRepository;
import doanjava1com.example.demo1.Repositories.IItemInvoiceRepository;
import doanjava1com.example.demo1.Repositories.UserRepository;
import doanjava1com.example.demo1.daos.Cart;
import doanjava1com.example.demo1.daos.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private static final String CART_SESSION_KEY = "cart";

    private final IInvoiceRepository invoiceRepository;
    private final IItemInvoiceRepository itemInvoiceRepository;
    private final ClothRepository clothRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyAuthority('USER') or hasAnyAuthority('ADMIN')")
    public Cart getCart(@NotNull HttpSession session) {
        return Optional.ofNullable((Cart) session.getAttribute(CART_SESSION_KEY))
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    session.setAttribute(CART_SESSION_KEY, cart);
                    return cart;
                });
    }

    @PreAuthorize("hasAnyAuthority('USER') or hasAnyAuthority('ADMIN')")
    public void updateCart(@NotNull HttpSession session, Cart cart) {
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeCart(@NotNull HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    @PreAuthorize("hasAnyAuthority('USER') or hasAnyAuthority('ADMIN')")
    public int getSumQuantity(@NotNull HttpSession session) {
        return getCart(session).getCartItems().stream()
                .mapToInt(Item::getQuantity)
                .sum();
    }

    @PreAuthorize("hasAnyAuthority('USER') or hasAnyAuthority('ADMIN')")
    public double getSumPrice(@NotNull HttpSession session) {
        return getCart(session).getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @PreAuthorize("hasAnyAuthority('USER') or hasAnyAuthority('ADMIN')")
    public void saveCart(@NotNull HttpSession session) {
        var cart = getCart(session);
        if (cart.getCartItems().isEmpty()) return;

        // Lấy thông tin người dùng từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.getUserByUsername(username);

        var invoice = new Invoice();
        invoice.setInvoiceDate(new Date(new Date().getTime()));
        invoice.setPrice(getSumPrice(session));
        invoice.setStatus(Invoice.Status.PENDING.name()); // Đặt trạng thái mặc định là PENDING
        invoice.setUser(user); // Thiết lập người dùng cho hóa đơn
        invoiceRepository.save(invoice);

        cart.getCartItems().forEach(item -> {
            var items = new ItemInvoice();
            items.setInvoice(invoice);
            items.setQuantity(item.getQuantity());
            items.setCloth(clothRepository.findById(item.getClothId()).orElseThrow());
            itemInvoiceRepository.save(items);
        });

        removeCart(session);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void updateInvoiceStatus(Long invoiceId, String status) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setStatus(status);
        invoiceRepository.save(invoice);
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    public List<Invoice> getUserInvoices(Long userId) {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getUser().getId().equals(userId))
                .toList();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}
