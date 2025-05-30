package com.example.assetmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class AssetManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(AssetManagementApplication.class, args);
    }
}

@Entity
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
}

@Entity
class Employee {
    @Id
    private Long id;

    private String fullName;

    private String designation;

    // Getters and Setters
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getDesignation() { return designation; }
    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDesignation(String designation) { this.designation = designation; }
}

enum AssetStatus {
    AVAILABLE,
    ASSIGNED,
    RECOVERED
}

@Entity
class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate purchaseDate;

    private String conditionNotes;

    @Enumerated(EnumType.STRING)
    private AssetStatus status;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Employee assignedTo;

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public String getConditionNotes() { return conditionNotes; }
    public AssetStatus getStatus() { return status; }
    public Category getCategory() { return category; }
    public Employee getAssignedTo() { return assignedTo; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public void setConditionNotes(String conditionNotes) { this.conditionNotes = conditionNotes; }
    public void setStatus(AssetStatus status) { this.status = status; }
    public void setCategory(Category category) { this.category = category; }
    public void setAssignedTo(Employee assignedTo) { this.assignedTo = assignedTo; }
}

interface CategoryRepository extends JpaRepository<Category, Long> { }

interface EmployeeRepository extends JpaRepository<Employee, Long> { }

interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByNameContainingIgnoreCase(String name);
}

@Service
class CategoryService {
    @Autowired private CategoryRepository categoryRepository;

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category updated) {
        Category existing = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return categoryRepository.save(existing);
    }

    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }
}

@Service
class AssetService {
    @Autowired private AssetRepository assetRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private EmployeeRepository employeeRepository;

    public Asset addAsset(Asset asset, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        asset.setCategory(category);
        asset.setStatus(AssetStatus.AVAILABLE);
        return assetRepository.save(asset);
    }

    public List<Asset> listAssets() {
        return assetRepository.findAll();
    }

    public List<Asset> searchAssets(String name) {
        return assetRepository.findByNameContainingIgnoreCase(name);
    }

    public Asset updateAsset(Long id, Asset updated) {
        Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setName(updated.getName());
        asset.setConditionNotes(updated.getConditionNotes());
        asset.setPurchaseDate(updated.getPurchaseDate());
        return assetRepository.save(asset);
    }

    public Asset assignAsset(Long assetId, Long employeeId) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        if (asset.getStatus() != AssetStatus.AVAILABLE)
            throw new RuntimeException("Asset not available for assignment");
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        asset.setAssignedTo(employee);
        asset.setStatus(AssetStatus.ASSIGNED);
        return assetRepository.save(asset);
    }

    public Asset recoverAsset(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setAssignedTo(null);
        asset.setStatus(AssetStatus.RECOVERED);
        return assetRepository.save(asset);
    }

    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        if (asset.getStatus() == AssetStatus.ASSIGNED)
            throw new RuntimeException("Cannot delete an assigned asset");
        assetRepository.delete(asset);
    }
}

@RestController
@RequestMapping("/categories")
class CategoryController {
    @Autowired private CategoryService categoryService;

    @PostMapping
    public Category addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    @GetMapping
    public List<Category> listCategories() {
        return categoryService.listCategories();
    }
}

@RestController
@RequestMapping("/assets")
class AssetController {
    @Autowired private AssetService assetService;

    @PostMapping("/{categoryId}")
    public Asset addAsset(@RequestBody Asset asset, @PathVariable Long categoryId) {
        return assetService.addAsset(asset, categoryId);
    }

    @GetMapping
    public List<Asset> listAssets() {
        return assetService.listAssets();
    }

    @GetMapping("/search")
    public List<Asset> searchAssets(@RequestParam String name) {
        return assetService.searchAssets(name);
    }

    @PutMapping("/{id}")
    public Asset updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        return assetService.updateAsset(id, asset);
    }

    @PostMapping("/{assetId}/assign/{employeeId}")
    public Asset assignAsset(@PathVariable Long assetId, @PathVariable Long employeeId) {
        return assetService.assignAsset(assetId, employeeId);
    }

    @PostMapping("/{assetId}/recover")
    public Asset recoverAsset(@PathVariable Long assetId) {
        return assetService.recoverAsset(assetId);
    }

    @DeleteMapping("/{id}")
    public void deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
    }
}
