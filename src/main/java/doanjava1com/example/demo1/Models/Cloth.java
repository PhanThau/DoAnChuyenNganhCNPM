package doanjava1com.example.demo1.Models;

import javax.persistence.*;

@Entity
@Table(name = "cloth")
public class Cloth {
    @Id
    @Column(name = "cloth_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "price")
    private Long price;

    @Column(name = "quantity")
    private int quantity;

    @Column(name="isdeleted", columnDefinition = "boolean default false")
    private boolean isdeleted;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = true, length = 255)
    private String photourl;

    public Cloth() {
        super();
    }

    public Cloth(Long id, String title, Long price, int quantity, Category category) {
        super();
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.isdeleted = quantity == 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.quantity == 0) {
            this.isdeleted = true;
        } else {
            this.isdeleted = false;
        }
    }

    public boolean isIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(boolean isdeleted) {
        this.isdeleted = isdeleted;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}

