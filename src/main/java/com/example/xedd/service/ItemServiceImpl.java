package com.example.xedd.service;

import com.example.xedd.exception.*;
import com.example.xedd.model.Item;
import com.example.xedd.payload.PictureDtoRequest;
import com.example.xedd.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    @Value("${app.upload.dir:${user.home}}")
    private String uploadDirectory;  // relative to root
    private final Path uploads = Paths.get("uploads");

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    @Override
    public long createItem(Item item) {
        Item newItem = itemRepository.save(item);
        return newItem.getId();
    }

    public Collection<Item> getItems(String name) {
        if (name.isEmpty()) {
            return itemRepository.findAll();
        }
        else {
            return itemRepository.findAllByName(name);
        }
    }

    @Override
    public Optional<Item> getItemById(long id) {
       if (!itemRepository.existsById(id)) throw new RecordNotFoundException();
        return itemRepository.findById(id);
    }

    @Override
    public void updateItem(long id, Item item) {
        if (!itemRepository.existsById(id)) throw new RecordNotFoundException();
        Item existingItem = itemRepository.findById(id).get();
        existingItem.setName(item.getName());
        existingItem.setDescription(item.getDescription());
        //existingItem.setToPicture(item.getToPicture());
        itemRepository.save(existingItem);
    }

    @Override
    public void partialUpdateItem(long id, Map<String, String> fields) {
        if (!itemRepository.existsById(id)) throw new RecordNotFoundException();
        Item item = itemRepository.findById(id).get();
        for (String field : fields.keySet()) {
            switch (field.toLowerCase()) {
                case "name":
                    item.setName((String) fields.get(field));
                    break;
                case "description":
                    item.setDescription((String) fields.get(field));
                    break;
                //case "toPicture":
                //    item.setToPicture((String) fields.get(field));

            }
        }
        itemRepository.save(item);
    }

    @Override
    public void deleteItem(long id) {
        if (!itemRepository.existsById(id)) throw new RecordNotFoundException();
        //TODO also remove picture
        itemRepository.deleteById(id);
    }

    @Override
    public boolean itemExistsById(long id) {
        return  itemRepository.existsById(id);
    }

    @Override
    public long uploadPicture(PictureDtoRequest pictureDtoRequest){
        return  uploadPicture(pictureDtoRequest.getItemId(), pictureDtoRequest.getPicture());
    }

    @Override
    public long uploadPicture(long itemId,MultipartFile file){
        if (!itemRepository.existsById(itemId)) throw new RecordNotFoundException();
        Item item = itemRepository.findById(itemId).get();
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        Path copyLocation = this.uploads.resolve(file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + originalFilename + ". Please try again!");
        }
        item.setToPicture(copyLocation.toString());
        Item saved = itemRepository.save(item);
        return saved.getId();
    }

    @Override
    public Resource downloadPicture(long itemId) {
        if (!itemRepository.existsById(itemId)) throw new RecordNotFoundException();
        Item item = itemRepository.findById(itemId).get();
        String location = item.getToPicture();
        Path path = this.uploads.resolve(location);

        Resource resource = null;
        try {
            resource = new UrlResource(path .toUri());
            return resource;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deletePicture(long itemId) {
        if (!itemRepository.existsById(itemId)) throw new RecordNotFoundException();
        Item item = itemRepository.findById(itemId).get();
        String location = item.getToPicture();
        Path path = this.uploads.resolve(location);
        try {
            Files.deleteIfExists(path);
            item.setToPicture(null);
            itemRepository.save(item);
        }
        catch (IOException ex) {
            throw new RuntimeException("File not found");
        }        
    }

}

