package doanjava1com.example.demo1.Repositories;

import doanjava1com.example.demo1.Models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import doanjava1com.example.demo1.Models.Cloth;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClothRepository extends JpaRepository<Cloth, Long> {
    @Query("SELECT b FROM Cloth b WHERE b.isdeleted = false")
    Page<Cloth> findWithOutDelete(Pageable page);
    @Modifying
    @Query("UPDATE Cloth b set b.isdeleted = true where b.id = :id")
    void deleteBookById(long id);
    @Query("SELECT b FROM Cloth b WHERE CONCAT(b.title, ' ', ' ', b.category.name, ' ', b.price) LIKE %:keyword% AND b.isdeleted = false")
    public Page<Cloth> Search(Pageable page, @Param("keyword") String keyword);
    @Modifying
    @Query("DELETE FROM Cloth c WHERE c.category = :category")
    void deleteByCategory(@Param("category") Category category);

    Page<Cloth> findByCategory(Category category, Pageable pageable);

}
