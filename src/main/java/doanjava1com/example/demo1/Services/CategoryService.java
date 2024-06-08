package doanjava1com.example.demo1.Services;

import java.util.List;

import javax.transaction.Transactional;

import doanjava1com.example.demo1.Repositories.ClothRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doanjava1com.example.demo1.Models.Category;
import doanjava1com.example.demo1.Repositories.CategoryRepository;

@Service
@Transactional
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClothRepository clothRepository;

    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    public void save(Category category) {
        categoryRepository.save(category);
    }

    public Category get(long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public void delete(long id) {
        // Xóa tất cả sản phẩm thuộc loại này trước khi xóa loại sản phẩm
        Category category = get(id);
        if (category != null) {
            clothRepository.deleteByCategory(category);
            categoryRepository.deleteById(id);
        }
    }
}

