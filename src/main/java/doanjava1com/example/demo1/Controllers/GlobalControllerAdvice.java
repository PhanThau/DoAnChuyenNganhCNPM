package doanjava1com.example.demo1.Controllers;

import doanjava1com.example.demo1.Models.Category;
import doanjava1com.example.demo1.Services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("categories")
    public List<Category> populateCategories() {
        return categoryService.listAll();
    }
}
