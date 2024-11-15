package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.AddCommentRqDto;
import ru.practicum.shareit.item.dto.AddItemRqDto;
import ru.practicum.shareit.item.dto.UpdateItemRqDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> addNewItem(Long userId, AddItemRqDto newItemDto) {
        return post("", userId, newItemDto);
    }

    public ResponseEntity<Object> findItemById(Long userId, Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> findAllItemsByOwner(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, UpdateItemRqDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> addNewComment(Long userId, Long itemId, AddCommentRqDto commentDto) {
        Map<String, Object> parameters = Map.of("itemId", itemId);
        return post("/{itemId}/comment", userId, parameters, commentDto);
    }
}
