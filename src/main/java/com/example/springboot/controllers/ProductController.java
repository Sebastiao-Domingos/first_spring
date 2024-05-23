package com.example.springboot.controllers;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dtos.ProductRecordDTO;
import com.example.springboot.modals.ProductModal;
import com.example.springboot.repositories.ProductRepository;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import jakarta.validation.Valid;



@RestController
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductModal> saveProduct ( @RequestBody @Valid ProductRecordDTO productRecordDTO){
        var productModal = new ProductModal();
        BeanUtils.copyProperties(productRecordDTO, productModal);

        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModal));
    }

     @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct ( @PathVariable(value = "id") UUID id){
        Optional<ProductModal> produtoO = productRepository.findById(id);
        
        if(produtoO.isEmpty()){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");

        }
        return ResponseEntity.status(HttpStatus.OK).body(produtoO.get());
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModal>> getAllProducts(){
        List<ProductModal> products = productRepository.findAll();

        if(!products.isEmpty()){
            for(ProductModal product : products){
                UUID id = product.getIdProduct();
                product.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getOneProduct(id)).withSelfRel()); 
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct( @PathVariable(value = "id") UUID id , @RequestBody @Valid ProductRecordDTO productRecordDTO){
        Optional<ProductModal> productO = productRepository.findById(id);

        if (productO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }

        var productModal = productO.get();

        BeanUtils.copyProperties(productRecordDTO, productModal);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModal));
    }

    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct( @PathVariable(value = "id") UUID id){
        Optional<ProductModal> productO = productRepository.findById(id);

        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
        productRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted.");
    }

}
