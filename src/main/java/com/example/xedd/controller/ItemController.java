package com.example.xedd.controller;

import com.example.xedd.model.Item;
import com.example.xedd.service.FileStorageService;
import com.example.xedd.service.FileStorageServiceImpl;
import com.example.xedd.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping(value ="api/v1/items")
public class ItemController {

    @Autowired
    ItemService itemService;

    private List<Item> items = new ArrayList<>();

    //build create item rest api
    @PostMapping("")
    public ResponseEntity<Object> createItem(@RequestParam String name,
                                         @RequestParam String description,
                                         @RequestParam boolean isSeed,
                                         @RequestParam MultipartFile toPicture) {
        try {
            Item item = new Item();
            item.setName(name);
            item.setDescription(description);
            item.setSeed(isSeed);
            itemService.uploadPicture(itemService.createItem(item),toPicture);
            return ResponseEntity.ok("Item created");
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getItems() { return ResponseEntity.ok().body(itemService.getAllItems()); }
    public List<Item> fetchItems(@RequestParam(name="name", defaultValue="") String name,
                                 @RequestParam(name="description", defaultValue="") String description) {
        return (List<Item>) ResponseEntity.ok().body(itemService.getAllItems());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(itemService.getItemById(id));
    }
    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable("id") long id, @RequestBody Item item) {
        itemService.updateItem(id, item);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> updateItemPartial(@PathVariable("id") long id, @RequestBody Map<String, String> fields) {
        itemService.partialUpdateItem(id, fields);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}/toPicture")
    public ResponseEntity downloadFile(@PathVariable long id) {
        Resource resource = itemService.downloadFile(id);
        //String filename = itemService.getItemById(id).getToPicture();
        String mediaType = "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mediaType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
//    @PostMapping(value = "/files",
////            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE} )
//    public ResponseEntity<Object> uploadFile(Method1RequestDto method1Dto) {
//        long newId = methode1Service.uploadFile(method1Dto);
//
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
//                .buildAndExpand(newId).toUri();
//
//        return ResponseEntity.created(location).body(location);
//    }
//
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Object> deleteFile(@PathVariable long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

}

