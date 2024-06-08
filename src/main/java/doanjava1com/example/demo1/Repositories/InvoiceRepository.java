package doanjava1com.example.demo1.Repositories;

import doanjava1com.example.demo1.Models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByInvoiceDateBetween(Date startDate, Date endDate);
}
