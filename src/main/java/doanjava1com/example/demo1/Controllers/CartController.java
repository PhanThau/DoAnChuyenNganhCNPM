package doanjava1com.example.demo1.Controllers;

import com.sun.istack.NotNull;
import doanjava1com.example.demo1.Models.Invoice;
import doanjava1com.example.demo1.Models.User;
import doanjava1com.example.demo1.Services.CartService;
import doanjava1com.example.demo1.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public String showCart(HttpSession session,
                           @NotNull Model model) {
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("totalPrice", cartService.getSumPrice(session));
        model.addAttribute("totalQuantity", cartService.getSumQuantity(session));
        return "cloth/cart";
    }

    @GetMapping("/removeFromCart/{id}")
    public String removeFromCart(HttpSession session,
                                 @PathVariable Long id) {
        var cart = cartService.getCart(session);
        cart.removeItems(id);
        return "redirect:/cart";
    }

    @GetMapping("/updateCart/{id}/{quantity}")
    public String updateCart(HttpSession session,
                             @PathVariable Long id,
                             @PathVariable int quantity) {
        var cart = cartService.getCart(session);
        cart.updateItems(id, quantity);
        return "cloth/cart";
    }

    @GetMapping("/clearCart")
    public String clearCart(HttpSession session) {
        cartService.removeCart(session);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session) {
        cartService.saveCart(session);
        return "redirect:/cart";
    }

    @GetMapping("/admin/invoices")
    public String viewAllInvoices(Model model) {
        model.addAttribute("invoices", cartService.getAllInvoices());
        return "admin/invoices";
    }

//    @GetMapping("/user/invoices/{userId}")
//    public String viewUserInvoices(@PathVariable Long userId, Model model) {
//        model.addAttribute("invoices", cartService.getUserInvoices(userId));
//        return "user/invoices";
//    }

    @GetMapping("/user/invoices")
    public String viewUserInvoices(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.findByUsername(username);
        model.addAttribute("invoices", cartService.getUserInvoices(user.getId()));
        return "user/invoices";
    }

    @PostMapping("/admin/updateInvoiceStatus/{invoiceId}/{status}")
    public String updateInvoiceStatus(@PathVariable Long invoiceId, @PathVariable String status) {
        cartService.updateInvoiceStatus(invoiceId, status);
        return "redirect:/cart/admin/invoices";
    }
}
