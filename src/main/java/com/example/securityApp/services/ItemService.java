package com.example.securityApp.services;

import com.example.securityApp.entities.*;

import java.util.List;

public interface ItemService {
    Items addItem(Items item);
    List<Items>getAllItems();
    Items getItem(Long id);
    Items saveItem(Items item);
    void delete(Items items);
    List<Items>getItems(String key);
     List<Items>brands(String brand);
     Categories getCategory(Long id);
    List<Categories> getAllCategories();
    void addCategory(Categories categories);
    void editCat(Categories categories);
    void deleteCat(Categories categories);
    List<Countries>countries();
    void editCount(Countries countries);
    void addCountry(Countries country);
    Countries getCountry(Long id);
    void deleteCountry(Countries country);


    void deleteBrand(Brands brands);
    Brands brands(Long id);
    void addBrand(Brands brands);
    void editBrand(Brands brand);
    List<Brands>brands();
    List<Comments> getAllComment(Long id);
    void addCom(Comments comment);



}
