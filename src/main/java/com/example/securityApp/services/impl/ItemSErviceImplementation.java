package com.example.securityApp.services.impl;

import com.example.securityApp.entities.*;
import com.example.securityApp.repository.*;
import com.example.securityApp.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemSErviceImplementation implements ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoriesRepsoitory categoriesRepsoitory;
    @Autowired
    private CountryRepsoitory countryRepsoitory;
    @Autowired
    private BrandsRepsoitory brandsRepsoitory;

    @Autowired
    private CommentRepository commentRepository;


    @Override
    public Items addItem(Items item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Items> getAllItems() {

        return itemRepository.findAll();
    }

    @Override
    public Items getItem(Long id) {
        Optional<Items> opt = itemRepository.findById(id);
        return opt.orElse(null);
    }

    public Items saveItem(Items item) {
        return itemRepository.save(item);
    }

    public void delete(Items item) {
        itemRepository.delete(item);
    }

    public List<Items> getItems(String key) {
        return itemRepository.findAllByNameContainsIgnoreCase(key);
    }

    public List<Items> brands(String brand) {
        return itemRepository.findAllByBrands_Name(brand);
    }

    public Categories getCategory(Long id) {
        Optional<Categories> category = categoriesRepsoitory.findById(id);
        return category.orElse(null);
    }
    public List<Categories> getAllCategories()
    {
      return categoriesRepsoitory.findAll();
    }

    public void addCategory(Categories categories)
    {
        categoriesRepsoitory.save(categories);
    }

    @Override
    public void editCat(Categories categories) {
        categoriesRepsoitory.save(categories);
    }
    public  void deleteCat(Categories categories)
    {
        categoriesRepsoitory.delete(categories);
    }

    public List<Countries>countries()
    {
        return countryRepsoitory.findAll();
    }

    public void editCount(Countries countries) {
        countryRepsoitory.save(countries);
    }

    public void addCountry(Countries country)
    {
        countryRepsoitory.save(country);
    }

    public Countries getCountry(Long id)
    {
        Optional<Countries>country=countryRepsoitory.findById(id);
        return country.orElse(null);
    }
    public void deleteCountry(Countries country)
    {
        countryRepsoitory.delete(country);
    }




    public List<Brands>brands()
    {
        return brandsRepsoitory.findAll();
    }

    public void editBrand(Brands brand) {

        brandsRepsoitory.save(brand);
    }

    public void addBrand(Brands brands)
    {
        brandsRepsoitory.save(brands);
    }

    public Brands brands(Long id)
    {
        Optional<Brands>brand=brandsRepsoitory.findById(id);
        return brand.orElse(null);
    }
    public void deleteBrand(Brands brands)
    {

        brandsRepsoitory.delete(brands);
    }

    public List<Comments> getAllComment(Long id)
    {
        return commentRepository.findAllByItems_Id(id);
    }

    public void addCom(Comments comment)
    {
        commentRepository.save(comment);
    }

}

