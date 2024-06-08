package doanjava1com.example.demo1.Controllers;

import doanjava1com.example.demo1.Services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

@Controller
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/invoices")
    public String getInvoices(Model model) {
        model.addAttribute("invoices", invoiceService.findAll());
        model.addAttribute("totalRevenueToday", invoiceService.getTotalRevenueByDay(new Date()));
        model.addAttribute("totalRevenueThisWeek", invoiceService.getTotalRevenueByWeek(new Date()));
        model.addAttribute("totalRevenueThisMonth", invoiceService.getTotalRevenueByMonth(new Date()));
        return "invoices";
    }
}
