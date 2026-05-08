package com.ecommerce.product.service;

import com.ecommerce.category.model.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.dto.*;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.ProductReview;
import com.ecommerce.product.model.Seller;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.ProductReviewRepository;
import com.ecommerce.product.repository.ProductSpecification;
import com.ecommerce.product.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto createProduct(ProductCreateRequest request) {
        log.debug("Creating new product: {}", request.getName());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        Seller seller = null;
        if (request.getSellerId() != null) {
            seller = sellerRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Seller not found with id: " + request.getSellerId()));
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);
        product.setEstimatedDelivery(request.getEstimatedDelivery());
        product.setFreeShipping(request.getFreeShipping() != null ? request.getFreeShipping() : false);
        product.setCategory(category);
        product.setSeller(seller);
        product.setActive(true);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully: {}", savedProduct.getName());

        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductUpdateRequest request) {
        log.debug("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        Seller seller = null;
        if (request.getSellerId() != null) {
            seller = sellerRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Seller not found with id: " + request.getSellerId()));
        }

        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setStockQuantity(request.getStockQuantity());
        product.setEstimatedDelivery(request.getEstimatedDelivery());
        product.setFreeShipping(request.getFreeShipping());
        product.setCategory(category);
        product.setSeller(seller);
        product.setCategory(category);
        
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        try {
            Product updatedProduct = productRepository.save(product);
            log.info("Product updated successfully: {}", updatedProduct.getName());
            return productMapper.toDto(updatedProduct);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure for product id: {}", id);
            throw new BusinessException("Product was updated by another user. Please refresh and try again.");
        }
    }

    @Override
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Soft delete - set active to false
        product.setActive(false);
        productRepository.save(product);
        
        log.info("Product soft deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        log.debug("Fetching all active products with pagination");

        return productRepository.findByActiveTrue(pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        log.debug("Fetching products for category: {}", categoryId);

        // Verify category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }

        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        log.debug("Searching products with query: {}", query);

        return productRepository.searchProducts(query, pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDto getProductDetail(Long id) {
        log.debug("Fetching product detail for id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        List<ProductReview> reviews = productReviewRepository.findByProductIdOrderByCreatedDateDesc(id);

        List<ProductImageDto> imageDtos = product.getImages().stream()
                .map(img -> new ProductImageDto(img.getId(), img.getUrl(), img.getDisplayOrder()))
                .collect(Collectors.toList());

        List<String> imageUrls = imageDtos.stream()
                .map(ProductImageDto::url)
                .collect(Collectors.toList());

        List<ProductColorDto> colorDtos = product.getColors().stream()
                .map(c -> new ProductColorDto(c.getId(), c.getName(), c.getHex(), c.getAvailable()))
                .collect(Collectors.toList());

        List<ProductOfferDto> offerDtos = product.getOffers().stream()
                .map(o -> new ProductOfferDto(o.getId(), o.getIconType(), o.getTitle(), o.getDescription()))
                .collect(Collectors.toList());

        List<ProductSpecificationDto> specDtos = product.getSpecifications().stream()
                .map(s -> new ProductSpecificationDto(s.getId(), s.getLabel(), s.getValue(), s.getDisplayOrder()))
                .collect(Collectors.toList());

        List<ProductReviewDto> reviewDtos = reviews.stream()
                .map(r -> new ProductReviewDto(r.getId(), r.getReviewerName(), r.getAvatarInitials(),
                        r.getRating(), r.getReviewDate(), r.getComment(), r.getHelpfulCount()))
                .collect(Collectors.toList());

        List<ProductQnADto> qnaDtos = product.getQna().stream()
                .map(q -> new ProductQnADto(q.getId(), q.getQuestion(), q.getAnswer(),
                        q.getAskedBy(), q.getAnsweredDate()))
                .collect(Collectors.toList());

        double avgRating = reviews.isEmpty() ? 0.0
                : reviews.stream().mapToInt(ProductReview::getRating).average().orElse(0.0);
        double roundedRating = Math.round(avgRating * 10.0) / 10.0;

        List<RatingDistributionDto> ratingDistribution = computeRatingDistribution(reviews);

        SellerDto sellerDto = null;
        if (product.getSeller() != null) {
            Seller s = product.getSeller();
            sellerDto = new SellerDto(s.getId(), s.getName(), s.getRating());
        }

        boolean inStock = product.getStockQuantity() != null && product.getStockQuantity() > 0;

        return new ProductDetailDto(
                product.getId(),
                product.getBrand(),
                product.getName(),
                roundedRating,
                (long) reviews.size(),
                product.getOriginalPrice(),
                product.getPrice(),
                product.getDiscountPercentage(),
                product.getDescription(),
                imageUrls,
                colorDtos,
                new ArrayList<>(product.getSizes()),
                inStock,
                product.getIsNew(),
                product.getEstimatedDelivery(),
                product.getFreeShipping(),
                sellerDto,
                offerDtos,
                specDtos,
                reviewDtos,
                ratingDistribution,
                qnaDtos
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> filterProducts(ProductFilterRequest filterRequest) {
        log.debug("Filtering products with criteria: categories={}, brands={}, colors={}, priceMin={}, priceMax={}",
                filterRequest.getCategories(), filterRequest.getBrands(), filterRequest.getColors(),
                filterRequest.getPriceMin(), filterRequest.getPriceMax());

        // Build specifications
        var spec = ProductSpecification.isActive();

        if (filterRequest.getCategories() != null && !filterRequest.getCategories().isEmpty()) {
            spec = spec.and(ProductSpecification.hasCategoryNames(filterRequest.getCategories()));
        }

        if (filterRequest.getBrands() != null && !filterRequest.getBrands().isEmpty()) {
            spec = spec.and(ProductSpecification.hasBrands(filterRequest.getBrands()));
        }

        if (filterRequest.getColors() != null && !filterRequest.getColors().isEmpty()) {
            spec = spec.and(ProductSpecification.hasColors(filterRequest.getColors()));
        }

        if (filterRequest.getPriceMin() != null || filterRequest.getPriceMax() != null) {
            spec = spec.and(ProductSpecification.priceIsBetween(
                    filterRequest.getPriceMin(),
                    filterRequest.getPriceMax()
            ));
        }

        if (Boolean.TRUE.equals(filterRequest.getInStock())) {
            spec = spec.and(ProductSpecification.inStock());
        }

        if (Boolean.TRUE.equals(filterRequest.getIsNew())) {
            spec = spec.and(ProductSpecification.isNew(true));
        }

        if (Boolean.TRUE.equals(filterRequest.getFreeShipping())) {
            spec = spec.and(ProductSpecification.hasFreeShipping(true));
        }

        if (filterRequest.getSearchQuery() != null && !filterRequest.getSearchQuery().isBlank()) {
            spec = spec.and(ProductSpecification.search(filterRequest.getSearchQuery()));
        }

        // Build pageable with sorting
        org.springframework.data.domain.Sort sort = filterRequest.getSortDir().equalsIgnoreCase("DESC")
                ? org.springframework.data.domain.Sort.by(filterRequest.getSortBy()).descending()
                : org.springframework.data.domain.Sort.by(filterRequest.getSortBy()).ascending();

        var pageable = org.springframework.data.domain.PageRequest.of(
                filterRequest.getPage(),
                filterRequest.getSize(),
                sort
        );

        return productRepository.findAll(spec, pageable).map(productMapper::toDto);
    }

    private List<RatingDistributionDto> computeRatingDistribution(List<ProductReview> reviews) {
        if (reviews.isEmpty()) {
            return List.of(
                    new RatingDistributionDto(5, 0),
                    new RatingDistributionDto(4, 0),
                    new RatingDistributionDto(3, 0),
                    new RatingDistributionDto(2, 0),
                    new RatingDistributionDto(1, 0)
            );
        }
        Map<Integer, Long> countByStar = reviews.stream()
                .collect(Collectors.groupingBy(ProductReview::getRating, Collectors.counting()));
        int total = reviews.size();
        return List.of(5, 4, 3, 2, 1).stream()
                .map(star -> {
                    int count = countByStar.getOrDefault(star, 0L).intValue();
                    int pct = (int) Math.round((count * 100.0) / total);
                    return new RatingDistributionDto(star, pct);
                })
                .collect(Collectors.toList());
    }
}
