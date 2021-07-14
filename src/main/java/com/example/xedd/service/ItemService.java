package com.example.xedd.service;

import com.example.xedd.dto.ItemRequestDto;
import com.example.xedd.model.Item;
import com.example.xedd.payload.PictureDtoRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ItemService {
    List<Item> getAllItems();
    long createItem(Item item);
    Collection<Item> getItems(String name);
    Optional<Item> getItemById(long id);
    void updateItem(long id, Item item);
    void partialUpdateItem(long id, Map<String, String> fields);
    void deleteItem(long id);
    public boolean itemExistsById(long id);
    long uploadPicture(PictureDtoRequest pictureDtoRequest);

    long uploadPicture(long itemId, MultipartFile file);

    Resource downloadPicture(long itemId);
    void deletePicture(long itemId);
}
